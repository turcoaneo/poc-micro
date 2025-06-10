package com.poc.microservices.employeeapp.repository;

import com.poc.microservices.employeeapp.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
}