package com.poc.microservices.main.app.service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;

@Component
public class KafkaMessageValidator {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageValidator.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonSchema schema;

    public KafkaMessageValidator() {
        try (InputStream is = getClass().getResourceAsStream("/kafka/employee-linked-event.schema.json")) {
            this.schema = JsonSchemaFactory
                    .getInstance(SpecVersion.VersionFlag.V7)
                    .getSchema(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON schema from resources", e);
        }
    }

    public boolean isValid(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            Set<ValidationMessage> errors = schema.validate(node);
            if (!errors.isEmpty()) {
                logger.warn("Schema validation failed: {}", errors);
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to validate message JSON", e);
            return false;
        }
    }
}