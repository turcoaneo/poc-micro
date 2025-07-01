package com.poc.microservices.main.app.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class KafkaEventSubscriberUnitTest {

    private KafkaEventSubscriber subscriber;

    @BeforeEach
    void setUp() {
        subscriber = new KafkaEventSubscriber();
    }

    @Test
    void shouldCollectIncomingMessage() {
        String mockMessage = "{ \"employeeId\": \"emp-999\" }";

        subscriber.listen(mockMessage);

        List<String> received = subscriber.getReceivedMessages();

        Assertions.assertFalse(received.isEmpty());
        Assertions.assertEquals(mockMessage, received.getFirst());
    }
}