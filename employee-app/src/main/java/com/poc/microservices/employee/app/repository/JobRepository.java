package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
}