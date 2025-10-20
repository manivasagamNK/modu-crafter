package com.moducrafter.appMod.events;

public class BANotificationEvent {
  private final int empId;
  private final String name;

  public BANotificationEvent(int empId, String name) {
    this.empId = empId;
    this.name = name;
  }

  public int getEmpId() { return empId; }
  public String getName() { return name; }
}
