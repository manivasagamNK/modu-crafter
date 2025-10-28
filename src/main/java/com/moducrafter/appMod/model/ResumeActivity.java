package com.moducrafter.appMod.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_activity")
public class ResumeActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "emp_id", nullable = false)
  private Employee employee;

  private String resumePath;   // Path to the file on the disk
  private String fileName;     // Original file name
  private String contentType;  // MIME type (application/pdf, etc.)
  private LocalDateTime uploadDate;

  // --- Constructors ---
  public ResumeActivity() {
    this.uploadDate = LocalDateTime.now();
  }

  // You should ensure the Employee entity has a public method getEmpId()

  // --- Getters and Setters (If not using Lombok) ---
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Employee getEmployee() { return employee; }
  public void setEmployee(Employee employee) { this.employee = employee; }

  public String getResumePath() { return resumePath; }
  public void setResumePath(String resumePath) { this.resumePath = resumePath; }

  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }

  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }

  public LocalDateTime getUploadDate() { return uploadDate; }
  public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
}
