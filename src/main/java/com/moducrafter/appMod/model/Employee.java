package com.moducrafter.appMod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "EMPLOYEE")
public class Employee {
    @Id
    @Column(name = "EMP_ID")
    private Integer empId;

    @Column(name = "NAME")
    private String name;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "PASSWORD_HASH")
     private String passwordHash;

    @Column(name = "DOJ")
    private LocalDate dateOfJoining;

    @Column(name = "TECH_STACK")
    @Lob
    private String techStack;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "RESUME")
    @Lob
    @JsonIgnore
    private byte[] resume;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<InterviewDetails> interviewDetailsList;

  @Column(name = "AMS_NAME")
  private String amsName;

  @Column(name = "MANAGER_NAME")
  private String managerName;

  @Column(name = "ROLE")
  private String role;

  @Column(name = "IS_BILLABLE")
  private Boolean isBillable;

  @Column(name = "INSERT_TS")
  private LocalDateTime updatedTime;


}
