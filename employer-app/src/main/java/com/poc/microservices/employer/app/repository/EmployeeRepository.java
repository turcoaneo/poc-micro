package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findEmployeesByEmployeeIdIn(@Param("employeeIds") List<Long> employeeIds);

    List<Employee> findByJobsJobId(Long jobId);
}

