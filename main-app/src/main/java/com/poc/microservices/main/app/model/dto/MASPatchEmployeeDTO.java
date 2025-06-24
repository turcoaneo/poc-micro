package com.poc.microservices.main.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MASPatchEmployeeDTO {
    private Long id;
    private String name;
    private Boolean active;
}