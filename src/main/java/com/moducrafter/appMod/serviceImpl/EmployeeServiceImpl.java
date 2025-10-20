package com.moducrafter.appMod.serviceImpl;

import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.events.BANotificationEvent;
import com.moducrafter.appMod.events.MappingUpdatedEvent;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.service.EmployeeService;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@XSlf4j
public class EmployeeServiceImpl implements EmployeeService {

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private InterviewDetailsService interviewDetailsService;
  @Autowired
  private ApplicationEventPublisher eventPublisher;
  private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

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
  public Employee addProfile(Employee employee) {
    log.info("Hey I'm adding Employee");
    Employee existingEmp = employeeRepository.findById(employee.getEmpId()).orElse(null);
    boolean isNewEmployee = existingEmp == null;

    // Determine current mapping status for an existing employee
    boolean isMappedBefore = existingEmp != null && !isUnmapped(existingEmp);
    Employee savedEmployee = employeeRepository.save(employee);
    createInitialInterviewEntry(savedEmployee);
    if (isNewEmployee || (existingEmp != null && !isMappedBefore)) {

      // Publish the event if mapping is required
      eventPublisher.publishEvent(
              new BANotificationEvent(savedEmployee.getEmpId(), savedEmployee.getName())
      );
    }

    return savedEmployee;

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

    // Step 2: Extract the supervisor's scope (AMS_NAME).
    // The AMS is defined by the project/team name they oversee.
    String scopeAmsName = supervisor.getAmsName();

    if (scopeAmsName == null || scopeAmsName.trim().isEmpty()) {
      return List.of();
    }

    return employeeRepository.findByRoleAndAmsName("AMC", scopeAmsName);
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
}
