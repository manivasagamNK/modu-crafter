package com.moducrafter.appMod.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class InterviewDetailsRequest {
    // Employee ID to link the interview (Foreign Key)
    private int empId;

    private LocalDate interviewDate;
    private String clientName;
    private String technologyStack;
    private String feedback;
    private String result;
    private LocalDateTime lastUpdatedTs;
}
