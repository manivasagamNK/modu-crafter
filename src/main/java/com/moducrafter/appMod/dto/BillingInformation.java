package com.moducrafter.appMod.dto;

import lombok.Data;

@Data
public class BillingInformation {
  private int totalEmployees;
  private int billableEmployees;
  private int nonBillableEmployees;
  private int risk1Employees;
  private int risk2Employees;
  private int risk3Employees;
}
