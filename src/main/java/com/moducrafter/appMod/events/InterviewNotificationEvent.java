package com.moducrafter.appMod.events;

import com.moducrafter.appMod.model.InterviewDetails;

public class InterviewNotificationEvent {
  private final InterviewDetails details;
  private final String employeeName;
  private final String action; // "SCHEDULED" or "UPDATED_FEEDBACK"

  public InterviewNotificationEvent(InterviewDetails details, String employeeName, String action) {
    this.details = details;
    this.employeeName = employeeName;
    this.action = action;
  }

  // Getters...
  public InterviewDetails getDetails() { return details; }
  public String getEmployeeName() { return employeeName; }
  public String getAction() { return action; }
}
