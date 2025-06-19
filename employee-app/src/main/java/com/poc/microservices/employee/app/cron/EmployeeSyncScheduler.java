package com.poc.microservices.employee.app.cron;

import com.poc.microservices.employee.app.model.dto.GrpcEmployerJobDtoList;
import com.poc.microservices.employee.app.proto.GrpcEmployeeClientService;
import com.poc.microservices.employee.app.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmployeeSyncScheduler {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeSyncScheduler.class);

    private GrpcEmployeeClientService grpcService;
    private EmployeeService employeeService;

    @Scheduled(cron = "${eem.scheduler.cron}")
    public void runEmployeeReconciliation() {
        logger.info("Reconciliation triggered by schedule");

        // Inject via config or env for now
        Integer employeeId = 1;
        List<Integer> ids = List.of(employeeId);
        GrpcEmployerJobDtoList dtoList = grpcService.getEmployerJobInfo(ids);

        if (dtoList == null) {
            logger.warn("Reconciliation failed for [employeeId={}]", employeeId);
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