package com.moducrafter.appMod.service;

import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@XSlf4j
public class AppModServiceImpl implements AppModService{

    @Autowired
    private EmployeeRepository employeeRepository;

  @Autowired
  private InterviewDetailsService interviewDetailsService;

  private static final Logger log = LoggerFactory.getLogger(AppModServiceImpl.class);

    @Override
    public Employee getEmployee(int id){
        log.info("{}", id);
        return employeeRepository.findByEmpId(id).orElse(null);
    }
    @Override
    public List<Employee> getEmployees(){
        return employeeRepository.findAll();
    }

    @Override
    @Transactional
    public Employee addProfile(Employee employee) {
      log.info("Hey I'm adding Employee");
      Employee savedEmp = employeeRepository.save(employee);
      createInitialInterviewEntry(savedEmp);

      return savedEmp;

    }

  private void createInitialInterviewEntry(Employee employee) {
    InterviewDetails initialEntry = new InterviewDetails();

    // Set the Foreign Key relationship
    initialEntry.setEmployee(employee);

    // 1. Tech Stack (from Employee)
    initialEntry.setTechnologyStack(employee.getTechStack());

    // 2. Placeholder/Default values
    initialEntry.setClientName("N/A - Initial Entry");
    initialEntry.setResult("N/A - Initial Entry");
    initialEntry.setFeedback("N/A - Initial Entry.");

    // 3. Date/Time fields
    initialEntry.setInterviewDate(null); // Assuming INTERVIEW_DATE is nullable
    //initialEntry.setCreatedTs(LocalDateTime.now());

    // 4. Save using InterviewDetailsService
    interviewDetailsService.saveNewInterview(initialEntry);
  }
}
