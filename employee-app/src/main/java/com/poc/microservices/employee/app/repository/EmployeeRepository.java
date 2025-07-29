package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e.employeeId FROM Employee e")
    List<Long> findAllEmployeeIds();

    List<Employee> findByName(String name);
}