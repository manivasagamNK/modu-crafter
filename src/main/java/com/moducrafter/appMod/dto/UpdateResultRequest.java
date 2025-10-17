package com.moducrafter.appMod.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateResultRequest {
    private String result;
    private String feedback;
    private LocalDate interviewDate;
}
