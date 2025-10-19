package com.moducrafter.appMod.controller;


import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.service.AIUdemyService;
import com.moducrafter.appMod.service.AppModService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/employee")
public class AppModController {
    private static final Logger log = LoggerFactory.getLogger(AppModController.class);
    @Autowired
    private AppModService appModService;
  @Autowired
  private AIUdemyService aiUdemyService;

    @GetMapping
    public ResponseEntity getEmployeeName(@RequestParam int id){
        log.info(String.valueOf(id));
        Employee emp = appModService.getEmployee(id);
        return ResponseEntity.status(HttpStatus.OK).body(Objects.requireNonNullElse(emp, "Please correct the Employee Id"));
    }

    @GetMapping("/getEmployees")
    public ResponseEntity<List<Employee>> getEmployees(){
        return ResponseEntity.ok(appModService.getEmployees());
    }


  @PostMapping(value = "addProfile", consumes = {"multipart/form-data"})
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

      // UPDATE EMPLOYEE OBJECT
      if (!extractedSkills.isEmpty()) {
        emp.setTechStack(extractedSkills);
        log.info("Successfully extracted {} skills.", extractedSkillsList.size());
      } else {
        log.warn("No technical skills were extracted for employee: {}", emp.getName());
      }

      // Save the Employee profile
      Employee savedEmp = appModService.addProfile(emp);
      return ResponseEntity.ok(savedEmp);

    } catch (Exception e) {
      log.error("Failed to process employee profile or extract skills.", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(null);
    }
  }

}
