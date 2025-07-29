package com.poc.microservices.employee.app.config;

import com.poc.microservices.employee.app.cron.EmployeeSyncScheduler;
import com.poc.microservices.employee.app.cron.SchedulerProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;
import java.time.Instant;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SchedulerConfig implements SchedulingConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);

    private final SchedulerProperties schedulerProperties;
    private final EmployeeSyncScheduler employeeSyncScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        String cron = schedulerProperties.getCron();
        logger.info("Registering cron task with expression: {}", cron);
        logger.info("Current time: {}", Instant.now());

        registrar.addFixedRateTask(() ->
                        logger.info("Heartbeat triggered at {}", Instant.now()),
                Duration.ofSeconds(300)
        );

        if (cron == null || cron.trim().isEmpty()) {
            logger.warn("Cron expression is missing or empty — scheduled task will not be registered.");
        } else if (!schedulerProperties.isEnabled()) {
            logger.info("Scheduler is disabled in config — skipping registration.");
        } else {
            registrar.addCronTask(
                    employeeSyncScheduler::runEmployeeReconciliation,
                    cron
            );
        }
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("ScheduledTask-");
        return scheduler;
    }
}