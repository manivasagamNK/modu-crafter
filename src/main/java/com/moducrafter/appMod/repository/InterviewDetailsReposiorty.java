package com.moducrafter.appMod.repository;

import com.moducrafter.appMod.model.InterviewDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewDetailsReposiorty extends JpaRepository<InterviewDetails,Integer> {
    List<InterviewDetails> findAllByOrderByInterviewDateDesc();
    List<InterviewDetails> findByEmployeeEmpIdOrderByInterviewDateDesc(int empId);


}
