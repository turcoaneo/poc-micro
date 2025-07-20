package com.poc.microservices.main.app.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RestartRequest {
    private String exePath;
    private String filePath;
    private String serviceName;
    private String jarName;
    private String port;
    private String healthPath;

}