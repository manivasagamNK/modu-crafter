package com.moducrafter.appMod.serviceImpl;

import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.events.BANotificationEvent;
import com.moducrafter.appMod.events.MappingUpdatedEvent;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.model.ResumeActivity;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.repository.ResumeActivityRepository;
import com.moducrafter.appMod.service.EmployeeService;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@XSlf4j
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private InterviewDetailsService interviewDetailsService;
  @Autowired
  private ResumeActivityRepository resumeActivityRepository;
  @Autowired
  private ApplicationEventPublisher eventPublisher;
  private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
  private static final List<String> ALLOWED_TYPES = Arrays.asList(
    "application/pdf",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // for .docx
  );
  private final Path fileStorageLocation = Paths.get("uploads/resumes").toAbsolutePath().normalize();

  @Override
  public Employee getEmployee(int id){
    log.info("{}", id);
    return employeeRepository.findByEmpId(id).orElse(null);
  }

  @Override
  public List<Employee> getEmployees(){
    return employeeRepository.findAll();
  }

  @Override
  @Transactional
  public Employee addProfile(Employee employee, MultipartFile file) throws Exception {
    log.info("Hey I'm adding Employee with EmpId: {}", employee.getEmpId());

    validateFile(file);

    // 1. Check existing state before saving
    Employee existingEmp = employeeRepository.findById(employee.getEmpId()).orElse(null);
    boolean isNewEmployee = existingEmp == null;
    boolean isMappedBefore = existingEmp != null && !isUnmapped(existingEmp);

    // 2. Save Employee (to ensure it is persisted and ID is generated/merged)
    Employee savedEmployee = employeeRepository.save(employee);

    // 3. Save file to disk
    String filePath = saveFileToStorage(file, savedEmployee.getEmpId());

    // 4. Save resume activity to the database
    saveResumeActivity(savedEmployee, file, filePath);

    // 5. Create initial interview entry
    createInitialInterviewEntry(savedEmployee);

    // 6. Publish events
    if (isNewEmployee || (existingEmp != null && !isMappedBefore)) {
      eventPublisher.publishEvent(
        new BANotificationEvent(savedEmployee.getEmpId(), savedEmployee.getName())
      );
    }

    return savedEmployee;

  }

  private void saveResumeActivity(Employee employee, MultipartFile file, String filePath) {
    ResumeActivity activity = new ResumeActivity();
    activity.setEmployee(employee);
    activity.setResumePath(filePath);
    activity.setFileName(file.getOriginalFilename());
    activity.setContentType(file.getContentType());

    resumeActivityRepository.save(activity);
  }

  // --- RE-IMPLEMENTED saveFileToStorage (Disk I/O) ---
  private String saveFileToStorage(MultipartFile file, Integer empId) throws IOException {
    // Note: Creating directories inside a transactional method is generally okay
    // as file system operations are separate from DB transactions.
    Files.createDirectories(this.fileStorageLocation);

    // Ensure file name uniqueness by prefixing with empId
    String fileName = empId + "_" + file.getOriginalFilename();
    Path targetLocation = this.fileStorageLocation.resolve(fileName);

    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

    return targetLocation.toString();
  }

  public Optional<ResumeActivity> getLatestResumeActivity(Integer empId) {
    return resumeActivityRepository.findFirstByEmployeeEmpIdOrderByUploadDateDesc(empId);
  }

  @Override
  public List<Employee> findEmployeesNeedingMapping() {
    return employeeRepository.findByRoleIsNullOrAmsNameIsNullOrManagerNameIsNull();
  }

  @Override
  public Employee updateEmployeeMapping(MappingDTO dto) {
    Employee employee = employeeRepository.findById(dto.getEmpId())
      .orElseThrow(() -> new RuntimeException("Employee not found"));

    employee.setRole(dto.getRole());
    employee.setAmsName(dto.getAmsName());
    employee.setManagerName(dto.getManagerName());
    employee.setIsBillable(dto.getIsBillable());
    employee.setUpdatedTime(LocalDateTime.now());

    Employee updatedEmployee=  employeeRepository.save(employee);
    eventPublisher.publishEvent(
      new MappingUpdatedEvent(
        updatedEmployee.getEmpId(),
        updatedEmployee.getName(),
        updatedEmployee.getRole(),
        updatedEmployee.getAmsName(),
        updatedEmployee.getManagerName()
      )
    );

    return updatedEmployee;
  }

  @Override
  @Transactional
  public Employee updateBillableStatus(int empId, Boolean isBillable) {

    Employee employee = employeeRepository.findById(empId)
      .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));

    employee.setIsBillable(isBillable);
    employee.setUpdatedTime(LocalDateTime.now());

    return employeeRepository.save(employee);
  }


  @Override
  public List<Employee> findAMCsBySupervisorScope(int supervisorId) {
    Employee supervisor = employeeRepository.findById(supervisorId)
      .orElseThrow(() -> new RuntimeException("Supervisor not found with ID: " + supervisorId));


    String scopeAmsName = supervisor.getName();

    if (scopeAmsName == null || scopeAmsName.trim().isEmpty()) {
      return List.of();
    }
// get it from UI
    return employeeRepository.findByRoleAndAmsName("amc", scopeAmsName);
  }


  private void createInitialInterviewEntry(Employee employee) {
    InterviewDetails initialEntry = new InterviewDetails();

    initialEntry.setEmployee(employee);

    initialEntry.setTechnologyStack(employee.getTechStack());

    initialEntry.setClientName("N/A - Initial Entry");
    initialEntry.setResult("N/A - Initial Entry");
    initialEntry.setFeedback("N/A - Initial Entry.");

    initialEntry.setInterviewDate(null);
    //initialEntry.setCreatedTs(LocalDateTime.now());

    // 4. Save using InterviewDetailsService
    interviewDetailsService.saveNewInterview(initialEntry);
  }

  // Check if the employee is currently unmapped
  private boolean isUnmapped(Employee emp) {
    return emp.getRole() == null && emp.getAmsName() == null && emp.getManagerName() == null;
  }

  /**
   * Validates the file for emptiness, size, and type (PDF or DOCX).
   * @throws Exception if validation fails.
   */
  private void validateFile(MultipartFile file) throws Exception{

    if (file == null || file.isEmpty()) {
      throw new Exception("Resume file is required.");
    }


    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
      throw new Exception("Unsupported file type. Only PDF and DOCX are allowed.");
    }
  }
}
