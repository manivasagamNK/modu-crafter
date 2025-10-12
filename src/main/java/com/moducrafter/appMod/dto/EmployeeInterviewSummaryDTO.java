package com.moducrafter.appMod.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.Date;

@Data
public class EmployeeInterviewSummaryDTO {
    private int empId;
    private String name;
    private String techStack; // comes from Employee table
    private String recentClient;
    private LocalDate recentDate;
    private String recentResult;
    private String recentFeedback;

    public EmployeeInterviewSummaryDTO(int empId, String name, String techStack, String recentClient, LocalDate recentDate, String recentResult, String recentFeedback) {
        this.empId = empId;
        this.name = name;
        this.techStack = techStack;
        this.recentClient = recentClient;
        this.recentDate = recentDate;
        this.recentResult = recentResult;
        this.recentFeedback = recentFeedback;
    }
}