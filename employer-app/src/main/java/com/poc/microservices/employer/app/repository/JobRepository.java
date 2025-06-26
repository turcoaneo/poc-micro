package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployerEmployerId(Long employerId);

    @Query("SELECT j.jobId FROM Job j WHERE j.employer.employerId = :employerId")
    Set<Long> findJobIdsByEmployerId(@Param("employerId") Long employerId);
}
