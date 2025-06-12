package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Job;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Job j WHERE j.jobId IN :jobIds")
    void deleteByIds(@Param("jobIds") Set<Long> jobIds);


}