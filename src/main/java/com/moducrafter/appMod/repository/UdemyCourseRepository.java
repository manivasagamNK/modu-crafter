package com.moducrafter.appMod.repository;

import com.moducrafter.appMod.model.UdemyCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UdemyCourseRepository extends JpaRepository<UdemyCourse, Long> {

  List<UdemyCourse> findByCategory(String category);

  List<UdemyCourse> findByCategoryContainingIgnoreCase(String keyword);

  List<UdemyCourse> findByCourseNameContainingIgnoreCase(String courseName);
}

