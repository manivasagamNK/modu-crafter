package com.moducrafter.appMod.repository;

import com.moducrafter.appMod.model.ResumeActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeActivityRepository extends JpaRepository<ResumeActivity, Long> {
  Optional<ResumeActivity> findFirstByEmployeeEmpIdOrderByUploadDateDesc(Integer empId);
}
