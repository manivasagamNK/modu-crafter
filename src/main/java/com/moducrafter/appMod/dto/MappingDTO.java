package com.moducrafter.appMod.dto;

import lombok.Data;

@Data
public class MappingDTO {
    private int empId;
    private String role;
    private String amsName;
    private String managerName;
    private Boolean isBillable;
}
