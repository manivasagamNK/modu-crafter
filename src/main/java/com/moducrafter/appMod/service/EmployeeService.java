package com.moducrafter.appMod.service;

import com.moducrafter.appMod.dto.BillingInformation;
import com.moducrafter.appMod.dto.MappingDTO;
import com.moducrafter.appMod.model.Employee;

import java.util.List;

public interface EmployeeService {
    Employee getEmployee(int id);

    List<Employee> getEmployees();

    Employee addProfile(Employee employee);
   List<Employee> findEmployeesNeedingMapping();
   Employee updateEmployeeMapping(MappingDTO dto);
  Employee updateBillableStatus(int empId, Boolean isBillable);
  List<Employee> findAMCsBySupervisorScope(int supervisorId);
  BillingInformation fetchBillingInformation();
}
