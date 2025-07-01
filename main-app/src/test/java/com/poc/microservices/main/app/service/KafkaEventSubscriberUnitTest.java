package com.poc.microservices.main.app.service;

import com.poc.microservices.main.app.service.util.KafkaMessageValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class KafkaEventSubscriberTest {

    @Spy
    private KafkaMessageValidator validator = new KafkaMessageValidator();

    @InjectMocks
    private KafkaEventSubscriber subscriber;

    @BeforeEach
    void setUp() {
        Assertions.assertNotNull(validator);
    }

    @Test
    void shouldCollectValidMessage() {
        // Assuming this matches your JSON Schema
        String validMessage = """
            {
                "messageId": "msg-001",
                "employeeId": "emp-123",
                "jobId": "job-88",
                "employerId": "em-77",
                "workingHours": 40
            }
        """;

        subscriber.listen(validMessage);

        List<String> received = subscriber.getReceivedMessages();
        Assertions.assertEquals(1, received.size());
        Assertions.assertEquals(validMessage, received.getFirst());
    }

    @Test
    void shouldDiscardInvalidMessage() {
        String invalidMessage = """
            {
                "employeeId": "emp-123"
            }
        """;

        subscriber.listen(invalidMessage);

        List<String> received = subscriber.getReceivedMessages();
        Assertions.assertTrue(received.isEmpty(), "Expected no messages to be collected");
    }
}