package com.moducrafter.appMod.events;

import com.moducrafter.appMod.model.InterviewDetails;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BANotificationListener {
  @Async
  @EventListener
  public void handleNotificationEvent(BANotificationEvent event) {

    System.out.println("ALERT! New Employee Mapping Required");
    System.out.println("Employee: " + event.getName() + " (ID: " + event.getEmpId() + ")");
    System.out.println("ACTION: BA should assign Role/AMS/Manager for new employee.");
  }
  @Async
  @EventListener
  public void handleMappingUpdateNotification(MappingUpdatedEvent event) {

    System.out.println("\n--- AMS PORTAL NOTIFICATION ---");
    System.out.println("You have a new team member assigned:");
    System.out.println("New Employee: " + event.getName() + " (ID: " + event.getEmpId() + ")");
    System.out.println("Assigned Role: " + event.getRole() + "\n");


    System.out.println("\n--- EMPLOYEE PORTAL NOTIFICATION ---");
    System.out.println("Employee: " + event.getName() + " (ID: " + event.getEmpId() + ")");
    System.out.println("Hy! Your mapping details have been assigned.");
    System.out.println("Role: " + event.getRole());
    System.out.println("AMS Name: " + event.getAmsName());
    System.out.println("Manager: " + event.getManagerName());

  }
  @Async
  @EventListener
  public void handleInterviewNotification(InterviewNotificationEvent event) {

    InterviewDetails details = event.getDetails();
    String action = event.getAction();

    // --- AMC Interview Notification ---
    System.out.println("\n--- AMC INTERVIEW PORTAL NOTIFICATION ---");
    System.out.println("Employee: " + event.getEmployeeName() + " (ID: " + details.getEmployee().getEmpId() + ")");
    System.out.println("ALERT! Your interview schedule has been " + action + ".");

    if ("SCHEDULED".equals(action)) {
      System.out.println("New Interview Scheduled:");
    } else {
      System.out.println("Feedback/Result Updated for Interview ID: " + details.getInterviewId());
    }

    System.out.println("Technology: " + details.getTechnologyStack());
    System.out.println("Date: " + details.getInterviewDate());
    System.out.println("Client: " + details.getClientName());
    System.out.println("New Result: " + details.getResult());
    System.out.println("-----------------------------------------\n");
  }
}

