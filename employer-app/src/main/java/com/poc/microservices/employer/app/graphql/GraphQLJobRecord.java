package com.poc.microservices.employer.app.graphql;

import java.util.List;
public record GraphQLJobRecord(Long jobId, String title, List<GraphQLEmployeeRecord> employees) {}