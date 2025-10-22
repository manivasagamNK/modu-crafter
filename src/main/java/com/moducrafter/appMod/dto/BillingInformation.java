package com.moducrafter.appMod.dto;

import lombok.Data;

@Data
public class BillingInformation {
  private int totalEmployees;
  private int billableEmployees;
  private int nonBillableEmployees;
  private int highRiskEmployees;
  private int mediumRiskEmployees;
  private int lowRiskEmployees;
}
