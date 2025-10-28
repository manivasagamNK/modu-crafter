package com.moducrafter.appMod.controller;


import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.service.AIUdemyService;
import com.moducrafter.appMod.service.EmployeeService;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.persistence.EntityNotFoundException;
import com.moducrafter.appMod.model.ResumeActivity; // NEW
import org.slf4j.Logger;
import org.springframework.core.io.Resource; // NEW
import org.springframework.core.io.UrlResource; // NEW
import org.springframework.http.HttpHeaders; // NEW
import org.springframework.http.MediaType; // NEWimport org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AppModController {
  private static final Logger log = LoggerFactory.getLogger(AppModController.class);
  @Autowired
  private EmployeeService employeeService;
  @Autowired
  private InterviewDetailsService interviewDetailsService;
  @Autowired
  private AIUdemyService aiUdemyService;
  @Autowired
  private EmployeeRepository employeeRepository;

  @GetMapping
  public ResponseEntity getEmployeeName(@RequestParam int id){
    log.info(String.valueOf(id));
    Employee emp = employeeService.getEmployee(id);
    return ResponseEntity.status(HttpStatus.OK).body(Objects.requireNonNullElse(emp, "Please correct the Employee Id"));
  }

  @GetMapping("/employee/getEmployees")
  public ResponseEntity<List<Employee>> getEmployees(){
    return ResponseEntity.ok(employeeService.getEmployees());
  }


  @PostMapping(value = "/employee/addProfile")
  public ResponseEntity<?> addEmployee(@Validated @RequestPart("emp") Employee emp,@RequestPart("file") MultipartFile file) throws IOException {

    try {
      emp.setUpdatedTime(LocalDateTime.now());
      emp.setUsername(emp.getName()+emp.getEmpId());
      emp.setPasswordHash(emp.getName()+emp.getEmpId());
      Employee savedEmp = employeeService.addProfile(emp, file);
      return ResponseEntity.ok(savedEmp);

    } catch (Exception e) {
      log.error("Failed to process employee profile addition.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(null);
    }
  }
  @GetMapping("/profile/{empId}")
  public ResponseEntity<Employee> getEmployeeProfile(@PathVariable Integer empId) {

    Optional<Employee> employee = employeeRepository.findById(empId);

    if (employee.isPresent()) {
      return ResponseEntity.ok(employee.get()); // Returns 200 OK
    } else {
      // Returns 404 Not Found if the employee is not found
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }
  // --- NEW DOWNLOAD/VIEW RESUME ENDPOINT (for BA/AMS pages) ---
  @GetMapping("/employee/resume/{empId}")
  public ResponseEntity<Resource> downloadResume(@PathVariable Integer empId) {

    Optional<ResumeActivity> resumeOpt = employeeService.getLatestResumeActivity(empId);

    if (resumeOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    ResumeActivity resume = resumeOpt.get();
    Path filePath = Paths.get(resume.getResumePath()).normalize();

    try {
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists() || resource.isReadable()) {
        return ResponseEntity.ok()
          // Set the dynamic content type (PDF, DOCX)
          .contentType(MediaType.parseMediaType(resume.getContentType()))
          // Force the browser to display inline (instead of downloading)
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resume.getFileName() + "\"")
          .body(resource);
      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null);
      }
    } catch (MalformedURLException ex) {
      log.error("File path is invalid: {}", resume.getResumePath(), ex);
      return ResponseEntity.internalServerError().build();
    }
  }
  @PostMapping (value = "employee/extractSkills", consumes = {"multipart/form-data"})
  public ResponseEntity<Map<String, String>> extractSkillsFromResume(@RequestPart("file") MultipartFile file) {

    Map<String, String> response = new HashMap<>();

    try {
      // 1. Extract raw text from the file
      Map<String, Object> extractedData = aiUdemyService.extractCourseDetails(
        file.getInputStream(),
        file.getOriginalFilename()
      );
      String rawExtractedText = (String) extractedData.getOrDefault("content", "");
      String filteredText = aiUdemyService.getRelevantText(rawExtractedText);
      List<String> extractedSkillsList = aiUdemyService.extractSkills(rawExtractedText);
      String extractedSkills = String.join(", ", extractedSkillsList);

      if (!extractedSkills.isEmpty()) {
        response.put("techStack", extractedSkills);
      } else {
        response.put("techStack", "");
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      // log.error("Failed to extract skills from resume.", e);
      response.put("error", "Extraction failed: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(response);
    }
  }
  @GetMapping("/employee/mapping/new-joiners")
  public ResponseEntity<List<Employee>> getNewJoiners() {
    List<Employee> newJoiners = employeeService.findEmployeesNeedingMapping();
    return ResponseEntity.ok(newJoiners);
  }
  // Get employees assigned to the AMC Role
  @GetMapping("/employee/role/amc")
  public ResponseEntity<List<Employee>> getAMCEmployees() {
    List<Employee>list=employeeRepository.findByRole("amc");
    list.forEach(emp -> {
      Optional<ResumeActivity>resumeOpt= employeeService.getLatestResumeActivity(emp.getEmpId());
      resumeOpt.ifPresent(resume->
        emp.setResumeUrl("/employee/resume"+emp.getEmpId())
    );
    });
    return ResponseEntity.ok(list);
  }

  // Get employees assigned to the AMS Role
  @GetMapping("/employee/role/ams")
  public ResponseEntity<List<Employee>> getAMSEmployees() {
    List<Employee>list=employeeRepository.findByRole("ams");
    return ResponseEntity.ok(employeeRepository.findByRole("ams"));
  }

  @PostMapping("/employee/update")
  public ResponseEntity<String> updateEmployeeMapping(@RequestBody MappingDTO mappingData) {
    employeeService.updateEmployeeMapping(mappingData);
    return ResponseEntity.ok("Mapping updated successfully");
  }

  @GetMapping("/amc-details")
  public ResponseEntity<List<Employee>> getAMCTeamDetails(@RequestHeader("X-Auth-User-Id") int authUserId)
  {
    Employee supervisor = employeeRepository.findById(authUserId)
      .orElseThrow(() -> new EntityNotFoundException("Auth user not found."));

    if (!"AMS".equalsIgnoreCase(supervisor.getRole())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Unauthorized
    }

    List<Employee> amcTeam = employeeService.findAMCsBySupervisorScope(authUserId);
    return ResponseEntity.ok(amcTeam);
  }


  /**
   * PUT endpoint for the AMS to update the billable status of an AMC.
   * Payload: {"isBillable": true/false}
   */
  @PutMapping("/amc/{empId}/billable")
  public ResponseEntity<Employee> updateBillableStatus(
    @PathVariable int empId,
    @RequestBody Map<String, Boolean> request) {

    Boolean isBillable = request.get("isBillable");
    if (isBillable == null) {
      return ResponseEntity.badRequest().build();
    }
    Employee updatedEmployee = employeeService.updateBillableStatus(empId, isBillable);
    return ResponseEntity.ok(updatedEmployee);
  }


}
