package com.moducrafter.appMod.controller;

import com.moducrafter.appMod.dto.EmployeeInterviewSummaryDTO;
import com.moducrafter.appMod.dto.InterviewDetailsRequest;
import com.moducrafter.appMod.dto.UpdateResultRequest;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.service.InterviewDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "http://localhost:4200")
public class InterviewDetailsController {

    @Autowired
    private InterviewDetailsService interviewDetailsService;

    // 1. GET: Master Grid Data (Summary)
    // GET /api/interviews/summary
    @GetMapping("/summary")
    public ResponseEntity<List<EmployeeInterviewSummaryDTO>> getEmployeeInterviewSummary() {
        List<EmployeeInterviewSummaryDTO> summary = interviewDetailsService.getEmployeeInterviewSummary();
        return ResponseEntity.ok(summary);
    }

    //2. GET: Detail Modal Data (Employeewise)
    // GET /api/interviews/employee/123
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<InterviewDetails>> getInterviewsByEmployeeId(@PathVariable int empId) {
        try {
            List<InterviewDetails> details = interviewDetailsService.getInterviewsByEmployeeId(empId);
            if (details.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 3. POST: Create New Interview (Add Details button)
    // POST /api/interviews
    @PostMapping
    public ResponseEntity<InterviewDetails> addInterview(@RequestBody InterviewDetailsRequest request) {
        try {
            InterviewDetails savedDetails = interviewDetailsService.addInterviewDetails(request);
            return new ResponseEntity<>(savedDetails, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Employee ID not found
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // 4. PUT: Update Result/Feedback
    // PUT /api/interviews/501 (where 501 is the INTERVIEW_ID)
    @PutMapping("/{interviewId}")
    public ResponseEntity<InterviewDetails> updateInterviewDetails(
            @PathVariable int interviewId,
            @RequestBody UpdateResultRequest request) {

        try {
            InterviewDetails updatedDetails = interviewDetailsService.updateInterviewResult(interviewId, request);
            return ResponseEntity.ok(updatedDetails);
        } catch (EntityNotFoundException e) {
            // Interview ID not found
            return ResponseEntity.notFound().build();
        }
    }
}
