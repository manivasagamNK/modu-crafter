package com.moducrafter.appMod.controller;


import com.moducrafter.appMod.dto.BillingInformation;
import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.service.AIUdemyService;
import com.moducrafter.appMod.service.EmployeeService;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
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


  @PostMapping(value = "/employee/addProfile", consumes = {"multipart/form-data"})
  public ResponseEntity<Employee> addEmployee(@Validated @RequestPart("emp") Employee emp,
                                              @RequestPart("file") MultipartFile file) throws IOException {

    try {
      emp.setResume(file.getBytes());

      Map<String, Object> extractedData = aiUdemyService.extractCourseDetails(
        file.getInputStream(),
        file.getOriginalFilename()
      );
      String rawExtractedText = (String) extractedData.getOrDefault("content", "");

      List<String> extractedSkillsList = aiUdemyService.extractSkills(rawExtractedText);

      String extractedSkills = String.join(", ", extractedSkillsList);

      if (!extractedSkills.isEmpty()) {
        emp.setTechStack(extractedSkills);
        log.info("Successfully extracted {} skills.", extractedSkillsList.size());
      } else {
        log.warn("No technical skills were extracted for employee: {}", emp.getName());
      }
      emp.setUpdatedTime(LocalDateTime.now());
      Employee savedEmp = employeeService.addProfile(emp);
      return ResponseEntity.ok(savedEmp);

    } catch (Exception e) {
      log.error("Failed to process employee profile or extract skills.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(null);
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
    return ResponseEntity.ok(employeeRepository.findByRole("AMC"));
  }

  // Get employees assigned to the AMS Role
  @GetMapping("/employee/role/ams")
  public ResponseEntity<List<Employee>> getAMSEmployees() {
    return ResponseEntity.ok(employeeRepository.findByRole("AMS"));
  }

  @PostMapping("/employee/update")
  public ResponseEntity<Employee> updateEmployeeMapping(@RequestBody MappingDTO mappingData) {
    Employee updatedEmp = employeeService.updateEmployeeMapping(mappingData);
    return ResponseEntity.ok(updatedEmp);
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

  @GetMapping("/ba/getBillingInfo")
  public ResponseEntity<BillingInformation> getBillingInfo(){
    return ResponseEntity.ok(employeeService.fetchBillingInformation());

  }


}
