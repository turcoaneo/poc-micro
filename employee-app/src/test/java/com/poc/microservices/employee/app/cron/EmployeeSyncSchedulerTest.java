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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    @SuppressWarnings("unused")
    @MockitoSpyBean
    private EmployeeSyncScheduler scheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(employeeService, "employeeRepository", employeeRepository);
        ReflectionTestUtils.setField(scheduler, "employeeService", employeeService);
        ReflectionTestUtils.setField(scheduler, "grpcService", grpcService);
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