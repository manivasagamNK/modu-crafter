package com.moducrafter.appMod.service;

import com.moducrafter.appMod.model.Employee;

import java.util.List;

public interface AppModService {
    Employee getEmployee(int id);

    List<Employee> getEmployees();

    Employee addProfile(Employee employee);
}
