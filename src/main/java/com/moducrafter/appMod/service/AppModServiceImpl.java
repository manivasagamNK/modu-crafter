package com.moducrafter.appMod.service;

import com.moducrafter.appMod.model.Employee;
import com.moducrafter.appMod.repository.EmployeeRepository;
import lombok.extern.slf4j.XSlf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@XSlf4j
public class AppModServiceImpl implements AppModService{

    @Autowired
    private EmployeeRepository employeeRepository;

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
    public Employee addProfile(Employee employee) {
        log.info("Hey I'm adding Employee");
        return employeeRepository.save(employee);

    }
}
