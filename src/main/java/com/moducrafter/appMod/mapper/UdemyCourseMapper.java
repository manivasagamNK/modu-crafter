package com.moducrafter.appMod.mapper;

import com.moducrafter.appMod.dto.UdemyCourseDto;
import com.moducrafter.appMod.model.UdemyCourse;
import org.springframework.stereotype.Component;

@Component
public class UdemyCourseMapper {

  public UdemyCourseDto toDto(UdemyCourse entity) {
    return new UdemyCourseDto(
      entity.getId(),
      entity.getCourseName(),
      entity.getCategory(),
      entity.getCourseUrl(),
      entity.getDescription(),
      entity.getRelevanceScore()
    );
  }

  public UdemyCourse toEntity(UdemyCourseDto dto) {
    UdemyCourse course = new UdemyCourse();
    course.setCourseName(dto.getCourseName());
    course.setCategory(dto.getCategory());
    course.setCourseUrl(dto.getCourseUrl());
    course.setDescription(dto.getDescription());
    course.setRelevanceScore(dto.getRelevanceScore());
    return course;
  }

}
