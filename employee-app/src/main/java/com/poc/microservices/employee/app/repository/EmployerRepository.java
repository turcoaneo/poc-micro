package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
}