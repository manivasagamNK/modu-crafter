package com.moducrafter.appMod.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "INTERVIEW_DETAILS")
public class InterviewDetails {
    @Id
    @Column(name = "INTERVIEW_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int interviewId;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "EMP_ID", referencedColumnName = "EMP_ID",nullable = false)
    @JsonIgnore
    private Employee employee;
    @Column(name = "TECHNOLOGY_STACK")
    private String technologyStack; // Added based on dashboard screenshot
    @Column(name = "INTERVIEW_DATE")
    private LocalDate interviewDate;
    @Column(name = "CLIENT_NAME")
    private String clientName;
    @Column(name = "FEEDBACK")
    private String feedback;
    @Column(name = "RESULT")
    private String result;
    @Column(name = "CREATED_BY")
    private LocalDateTime lastUpdatedTs;
}
