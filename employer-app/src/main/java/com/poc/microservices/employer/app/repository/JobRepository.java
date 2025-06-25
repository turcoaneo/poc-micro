package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployerEmployerId(Long employerId);
}
