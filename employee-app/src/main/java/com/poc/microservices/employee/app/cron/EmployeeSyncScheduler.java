package com.poc.microservices.employee.app.cron;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import com.poc.microservices.employee.app.service.EmployeeService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
public class EmployeeSyncScheduler {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeSyncScheduler.class);

    private SchedulerProperties schedulerProperties;
    private GrpcEmployeeClientService grpcService;
    private EmployeeService employeeService;

    @Autowired
    public EmployeeSyncScheduler(SchedulerProperties schedulerProperties, GrpcEmployeeClientService grpcService,
                                 EmployeeService employeeService) {
        this.schedulerProperties = schedulerProperties;
        this.grpcService = grpcService;
        this.employeeService = employeeService;
    }


    @Scheduled(cron = "${eem.scheduler.cron}")
    public void runEmployeeReconciliation() {
        logger.info("SchedulerProperties instance: {}", System.identityHashCode(schedulerProperties));
        if (!schedulerProperties.isEnabled()) {
            logger.info("Scheduler is disabled. Skipping reconciliation.");
            return;
        }

        logger.info("Reconciliation triggered by schedule");

        List<Integer> ids = employeeService.findEmployeeIds();
        GrpcEmployerJobDtoList dtoList = grpcService.getEmployerJobInfo(ids);

        if (dtoList == null) {
            logger.warn("Reconciliation failed for [employeeIds={}]", ids);
            return;
        }

        dtoList.getEmployerJobDtos().forEach(dto -> {
            try {
                logger.info("Reconciliation triggered for [employee ID: {}]", dto.getEmployeeId());
                employeeService.reconcileEmployee(dto);
            } catch (Exception e) {
                // Log it and move on – no break in the chain
                logger.warn("Reconciliation failed for employeeId={}, employerId={}", dto.getEmployeeId(),
                        dto.getEmployerId(), e);
            }
        });
    }
}