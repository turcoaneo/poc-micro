package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Employer e WHERE e.employerId IN :employerIds")
    void deleteByIds(@Param("employerIds") Set<Long> employerIds);
}