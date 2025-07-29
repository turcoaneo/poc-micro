package com.poc.microservices.employee.app.cron;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import com.poc.microservices.employee.app.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class EmployeeSyncScheduler {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeSyncScheduler.class);

    private final SchedulerProperties schedulerProperties;
    private final GrpcEmployeeClientService grpcService;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeSyncScheduler(SchedulerProperties schedulerProperties, GrpcEmployeeClientService grpcService,
                                 EmployeeService employeeService) {
        this.schedulerProperties = schedulerProperties;
        this.grpcService = grpcService;
        this.employeeService = employeeService;
    }

    @PostConstruct
    public void init() {
        logger.info("EmployeeSyncScheduler initialized");
        logger.info("SchedulerProperties initialized {}", schedulerProperties.isEnabled());
    }


//    @Scheduled(cron = "0/1 * * * * *") // directly hard-coded
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