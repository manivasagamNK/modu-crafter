package com.moducrafter.appMod.controller;

import com.moducrafter.appMod.dto.InterviewDetailsRequest;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.service.AppModService;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.antlr.v4.runtime.misc.NotNull;
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
import java.util.Objects;

@RestController
@RequestMapping("/api/employee")
public class AppModController {
    private static final Logger log = LoggerFactory.getLogger(AppModController.class);
    @Autowired
    private AppModService appModService;

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
        emp.setResume(file.getBytes());
        Employee savedEmp = appModService.addProfile(emp);
        return ResponseEntity.ok(savedEmp);
    }

}
