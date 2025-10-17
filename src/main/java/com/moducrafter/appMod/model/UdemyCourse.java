package com.moducrafter.appMod.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "udemy_course")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
  public class UdemyCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "course_url")
    private String courseUrl;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "relevance_score")
    private Double relevanceScore;


  }

