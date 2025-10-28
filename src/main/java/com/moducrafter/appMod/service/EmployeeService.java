package com.moducrafter.appMod.service;

import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.model.ResumeActivity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee getEmployee(int id);

    List<Employee> getEmployees();

    Employee addProfile(Employee employee, MultipartFile file) throws Exception;
   List<Employee> findEmployeesNeedingMapping();
   Employee updateEmployeeMapping(MappingDTO dto);
  Employee updateBillableStatus(int empId, Boolean isBillable);
  List<Employee> findAMCsBySupervisorScope(int supervisorId);
   Optional<ResumeActivity> getLatestResumeActivity(Integer empId);


}
