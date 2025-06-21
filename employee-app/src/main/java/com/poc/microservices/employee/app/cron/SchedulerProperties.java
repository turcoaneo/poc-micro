package com.poc.microservices.employee.app.cron;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "eem.scheduler")
@Getter @Setter
public class SchedulerProperties {
    private boolean enabled;
}