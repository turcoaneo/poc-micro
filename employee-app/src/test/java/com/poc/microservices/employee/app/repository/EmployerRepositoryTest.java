package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Employer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmployerRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private EmployerRepository employerRepository;

    @Test
    void testDeleteByIds() {
        Employer employer1 = employerRepository.save(new Employer(null, "TechCorp", new HashSet<>()));
        Employer employer2 = employerRepository.save(new Employer(null, "DataLabs", new HashSet<>()));
        Employer employer3 = employerRepository.save(new Employer(null, "InnovateX", new HashSet<>()));

        Set<Long> employerIdsToDelete = Set.of(employer1.getEmployerId(), employer2.getEmployerId());

        employerRepository.deleteByIds(employerIdsToDelete);
        employerRepository.flush();

        entityManager.clear();

        Optional<Employer> removedEmployer = employerRepository.findById(employer1.getEmployerId());
        Assertions.assertTrue(removedEmployer.isEmpty()); // Employer should be deleted

        Optional<Employer> remainingEmployer = employerRepository.findById(employer3.getEmployerId());
        Assertions.assertFalse(remainingEmployer.isEmpty());
    }
}