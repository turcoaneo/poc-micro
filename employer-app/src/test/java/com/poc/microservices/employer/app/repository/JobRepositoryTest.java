package com.poc.microservices.employer.app.repository;

import com.poc.microservices.employer.app.model.Employer;
import com.poc.microservices.employer.app.model.Job;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class JobRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    private JobRepository jobRepository;
    private Long jobId;

    @BeforeEach
    public void setUp() {
        Employer employer = new Employer(null, "employer", new HashSet<>());
        Set<Job> jobs = employer.getJobs();
        Job job1 = new Job(null, "job 1", employer, "desc 1", 10D, new HashSet<>());
        Job job2 = new Job(null, "job 2", employer, "desc 2", 20D, new HashSet<>());
        jobs.add(job1);
        jobs.add(job2);
        testEntityManager.persist(employer);
        jobId = job2.getJobId();
    }

    @Test
    void testSaveAndFindJob() {
        Optional<Job> retrievedJob = jobRepository.findById(jobId);
        Assertions.assertTrue(retrievedJob.isPresent());
        Assertions.assertEquals("job 2", retrievedJob.get().getTitle());

        List<Job> byEmployerEmployerId = jobRepository.findByEmployerEmployerId(retrievedJob.get().getEmployer().getEmployerId());
        Assertions.assertEquals(2, byEmployerEmployerId.size());
    }

    @Test
    void testDeleteJob() {
        Optional<Job> retrievedJob = jobRepository.findById(jobId);
        Job job = retrievedJob.orElse(null);
        Assertions.assertNotNull(job);
        job.getEmployer().getJobs().remove(job);
        job.setEmployer(null);
        jobRepository.delete(job);
        jobRepository.flush();

        Optional<Job> deletedJob = jobRepository.findById(jobId);
        Assertions.assertTrue(deletedJob.isEmpty());
    }
}