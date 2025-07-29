package com.poc.microservices.employee.app.cron;

import com.poc.microservices.employee.app.model.Employee;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import com.poc.microservices.employee.app.repository.EmployeeRepository;
import com.poc.microservices.employee.app.service.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TestConfiguration
class TestSchedulerConfig implements SchedulingConfigurer {

    @Autowired
    private EmployeeSyncScheduler employeeSyncScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addCronTask(employeeSyncScheduler::runEmployeeReconciliation, "*/1 * * * * *");
    }
}

@Import(TestSchedulerConfig.class)
@SpringBootTest
@EnableScheduling
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeSyncSchedulerTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private GrpcEmployeeClientService grpcService;

    @MockitoBean
    private SchedulerProperties schedulerProperties;

    @SuppressWarnings("unused")
    @MockitoSpyBean
    private EmployeeSyncScheduler scheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(employeeService, "employeeRepository", employeeRepository);
        ReflectionTestUtils.setField(scheduler, "employeeService", employeeService);
        ReflectionTestUtils.setField(scheduler, "grpcService", grpcService);
        ReflectionTestUtils.setField(scheduler, "schedulerProperties", schedulerProperties);
        ReflectionTestUtils.setField(schedulerProperties, "enabled", true);
    }


    @Test
    void testSchedulerIsTriggered() throws InterruptedException {
        GrpcEmployerJobDto dto = new GrpcEmployerJobDto(1L, "Employee X", 100L, "Test Employer", Map.of(1L, "Dev"));
        GrpcEmployerJobDtoList dtoList = new GrpcEmployerJobDtoList(List.of(dto));
        Mockito.when(grpcService.getEmployerJobInfo(Mockito.anyList())).thenReturn(dtoList);

        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new Employee()));

        CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(invocation -> {
            latch.countDown(); // Count once it triggers
            return invocation.callRealMethod();
        }).when(scheduler).runEmployeeReconciliation();

        // Wait for max 2 seconds
        boolean triggered = latch.await(2, TimeUnit.SECONDS);
        Assertions.assertTrue(triggered, "Scheduler should trigger within seconds");
    }
}