package com.poc.microservices.main.app.service.util;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.configurationprocessor.json.JSONTokener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class SchemaProvider {

    private final Schema schema;

    public SchemaProvider() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/schema/employee-linked-event.schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(is));
            this.schema = SchemaLoader.load(rawSchema);
        }
    }

    public Schema getSchema() {
        return schema;
    }
}