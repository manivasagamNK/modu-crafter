package com.moducrafter.appMod.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
public class EmployeeInterviewSummaryDTO {
    private int empId;
    private String name;
    private String techStack;
    private String recentClient;
    private LocalDate recentDate;
    private String recentResult;
    private String recentFeedback;
}
