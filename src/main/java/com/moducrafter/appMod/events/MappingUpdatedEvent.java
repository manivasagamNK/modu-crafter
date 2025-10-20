package com.moducrafter.appMod.events;

public class MappingUpdatedEvent {
  private final int empId;
  private final String name;
  private final String role;
  private final String amsName;
  private final String managerName;

  public MappingUpdatedEvent(int empId, String name, String role, String amsName, String managerName) {
    this.empId = empId;
    this.name = name;
    this.role = role;
    this.amsName = amsName;
    this.managerName = managerName;
  }

  public int getEmpId() { return empId; }
  public String getName() { return name; }
  public String getRole() { return role; }
  public String getAmsName() { return amsName; }
  public String getManagerName() { return managerName; }
}
