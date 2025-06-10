package com.poc.microservices.employer.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private Set<Employee> employees = new HashSet<>();

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private Set<Job> jobs = new HashSet<>();  // Normalized Job entity reference
}