package com.moducrafter.appMod.controller;

import com.moducrafter.appMod.dto.LoginRequest;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private EmployeeRepository employeeRepository;
  /**
   * POST /api/auth/login
   * Authenticates using username and password from the frontend static logic.
   */
  @PostMapping("/login")
  public ResponseEntity<Employee> login(@RequestBody LoginRequest request) {

    Employee user = employeeRepository.findByUsername(request.getUsername())
      .orElseThrow(() -> new EntityNotFoundException("Invalid Username/Password."));

    if (!user.getPasswordHash().equals(request.getPassword())) {
      throw new EntityNotFoundException("Invalid Username/Password.");
    }

    return ResponseEntity.ok(user);
  }
}
