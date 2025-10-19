package com.moducrafter.appMod.service;

import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.model.Employee;

import java.util.List;

public interface EmployeeService {
    Employee getEmployee(int id);

    List<Employee> getEmployees();

    Employee addProfile(Employee employee);
   List<Employee> findEmployeesNeedingMapping();
   Employee updateEmployeeMapping(MappingDTO dto);
}
