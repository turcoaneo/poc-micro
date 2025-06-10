package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.EmployeeJobEmployer;
import com.poc.microservices.employee.app.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeJobEmployerRepository extends JpaRepository<EmployeeJobEmployer, Long> {

    @Query("SELECT ej.employee FROM EmployeeJobEmployer ej WHERE ej.job.id = :jobId AND ej.employer.id = :employerId")
    List<Employee> findEmployeesByJobAndEmployer(@Param("jobId") Long jobId, @Param("employerId") Long employerId);

    @Query("SELECT ej.job FROM EmployeeJobEmployer ej WHERE ej.employee.id = :employeeId")
    List<Job> findJobsByEmployeeId(@Param("employeeId") Long employeeId);

}