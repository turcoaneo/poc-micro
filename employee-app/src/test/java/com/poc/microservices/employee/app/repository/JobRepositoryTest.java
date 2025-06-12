package com.poc.microservices.employee.app.repository;

import com.poc.microservices.employee.app.model.Job;
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
class JobRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private JobRepository jobRepository;

    @Test
    void testDeleteByIds() {
        Job job1 = jobRepository.save(new Job(null, "Developer", new HashSet<>()));
        Job job2 = jobRepository.save(new Job(null, "Data Scientist", new HashSet<>()));
        Job job3 = jobRepository.save(new Job(null, "Architect", new HashSet<>()));

        Set<Long> jobIdsToDelete = Set.of(job1.getJobId(), job2.getJobId());

        jobRepository.deleteByIds(jobIdsToDelete);
        jobRepository.flush();

        entityManager.clear();

        Optional<Job> removedEmployer = jobRepository.findById(job1.getJobId());
        Assertions.assertTrue(removedEmployer.isEmpty()); // Employer should be deleted

        Optional<Job> remainingEmployer = jobRepository.findById(job3.getJobId());
        Assertions.assertFalse(remainingEmployer.isEmpty());
    }
}