package com.moducrafter.appMod.repository;

import com.moducrafter.appMod.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByEmpId(int id);
    List<Employee> findAll();
    Employee save(Employee emp);
  List<Employee> findByRole(String role);
  List<Employee> findByRoleIsNullOrAmsNameIsNullOrManagerNameIsNull();
  List<Employee> findByRoleAndAmsName(String role, String amsName);
  Optional<Employee> findByUsername(String username);
  List<Employee> findAllByIsBillableFalse();
  int countByIsBillableTrue();
  int countByIsBillableFalse();


}
