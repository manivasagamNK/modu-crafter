package com.moducrafter.appMod.dto;

import lombok.Data;

@Data
public class UdemyCourseDto {
  private Long id;
  private String courseName;
  private String category;
  private String courseUrl;
  private String description;
  private Double relevanceScore;

  public UdemyCourseDto() {}

  public UdemyCourseDto(Long id, String courseName, String category, String courseUrl, String description, Double relevanceScore) {
    this.id = id;
    this.courseName = courseName;
    this.category = category;
    this.courseUrl = courseUrl;
    this.description = description;
    this.relevanceScore = relevanceScore;
  }

}
