package com.moducrafter.appMod.serviceImpl;

import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.InterviewDetails;
import com.moducrafter.appMod.repository.EmployeeRepository;
import com.moducrafter.appMod.service.EmployeeService;
import com.moducrafter.appMod.service.InterviewDetailsService;
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
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

  @Autowired
  private InterviewDetailsService interviewDetailsService;

  private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

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

  @Override
  public List<Employee> findEmployeesNeedingMapping() {
    return employeeRepository.findByRoleIsNullOrAmsNameIsNullOrManagerNameIsNull();
    }

  @Override
  public Employee updateEmployeeMapping(MappingDTO dto) {
    Employee employee = employeeRepository.findById(dto.getEmpId())
      .orElseThrow(() -> new RuntimeException("Employee not found"));

    employee.setRole(dto.getRole());
    employee.setAmsName(dto.getAmsName());
    employee.setManagerName(dto.getManagerName());
    employee.setIsBillable(dto.getIsBillable());
    employee.setUpdatedTime(LocalDateTime.now());

    return employeeRepository.save(employee);
    }

  private void createInitialInterviewEntry(Employee employee) {
    InterviewDetails initialEntry = new InterviewDetails();

    initialEntry.setEmployee(employee);

    initialEntry.setTechnologyStack(employee.getTechStack());

    initialEntry.setClientName("N/A - Initial Entry");
    initialEntry.setResult("N/A - Initial Entry");
    initialEntry.setFeedback("N/A - Initial Entry.");

    initialEntry.setInterviewDate(null);
    //initialEntry.setCreatedTs(LocalDateTime.now());

    // 4. Save using InterviewDetailsService
    interviewDetailsService.saveNewInterview(initialEntry);
  }

}
