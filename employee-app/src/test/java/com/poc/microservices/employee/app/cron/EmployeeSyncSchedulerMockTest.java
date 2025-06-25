package com.poc.microservices.employee.app.cron;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDto;
import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import com.poc.microservices.employee.app.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

@SpringBootTest
@EnableScheduling
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeSyncSchedulerMockTest {

    @MockitoBean
    private GrpcEmployeeClientService grpcService;
    @MockitoBean
    private EmployeeService employeeService;
    @Autowired
    private SchedulerProperties schedulerProperties;

    @Autowired
    private EmployeeSyncScheduler scheduler;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(schedulerProperties, "enabled", true);
    }

    @Test
    void testEmployeeIsReconciled() {
        GrpcEmployerJobDto dto = new GrpcEmployerJobDto(1L, "Employee X", 100L, "Test Employer", Map.of(1L, "Dev"));
        GrpcEmployerJobDtoList dtoList = new GrpcEmployerJobDtoList(List.of(dto));

        Mockito.when(grpcService.getEmployerJobInfo(List.of(1))).thenReturn(dtoList);

        scheduler.runEmployeeReconciliation();

        Mockito.verify(employeeService).reconcileEmployee(dto);
    }
}