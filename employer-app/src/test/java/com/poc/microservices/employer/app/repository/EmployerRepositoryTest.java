package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Employer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmployerRepositoryTest {

    @Autowired
    private EmployerRepository employerRepository;

    @Test
    void testSaveAndFindEmployer() {
        Employer employer = employerRepository.save(new Employer(null, "TechCorp", new HashSet<>()));

        Optional<Employer> retrievedEmployer = employerRepository.findById(employer.getEmployerId());
        Assertions.assertTrue(retrievedEmployer.isPresent());
        Assertions.assertEquals("TechCorp", retrievedEmployer.get().getName());
    }

    @Test
    void testDeleteEmployer() {
        Employer employer = employerRepository.save(new Employer(null, "DataLabs", new HashSet<>()));
        Long employerId = employer.getEmployerId();

        employerRepository.deleteById(employerId);
        employerRepository.flush();

        Optional<Employer> deletedEmployer = employerRepository.findById(employerId);
        Assertions.assertTrue(deletedEmployer.isEmpty());
    }
}