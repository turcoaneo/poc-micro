package com.poc.microservices.employer.app.graphql;

import java.util.List;

public record GraphQLEmployerRecord(Long employerId, String name, List<GraphQLJobRecord> jobs) {}