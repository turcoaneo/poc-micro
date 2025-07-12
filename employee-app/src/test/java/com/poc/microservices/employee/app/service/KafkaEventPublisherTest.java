package com.poc.microservices.employee.app.service;

import com.poc.microservices.employee.app.kafka.KafkaEventPublisher;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


class KafkaEventPublisherTest {

    private KafkaProducer<String, String> mockedProducer;
    private KafkaEventPublisher publisher;

    @BeforeEach
    void setUp() {
        //noinspection unchecked
        mockedProducer = Mockito.mock(KafkaProducer.class);

        publisher = new KafkaEventPublisher("127.0.0.1", "false") {
            @Override
            public void publishEvent(String key, String event) {
                mockedProducer.send(new ProducerRecord<>("eem.employees.linked", key, event));
            }

            @Override
            public void close() {
                mockedProducer.close();
            }
        };
    }

    @Test
    void shouldPublishEventToKafkaTopic() {
        String testKey = "test-key-123";
        String testEvent = "{ \"employeeId\": \"emp-42\" }";

        // when
        publisher.publishEvent(testKey, testEvent);

        // then
        //noinspection unchecked
        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        Mockito.verify(mockedProducer).send(captor.capture());

        ProducerRecord<String, String> record = captor.getValue();
        Assertions.assertEquals("eem.employees.linked", record.topic());
        Assertions.assertEquals(testKey, record.key());
        Assertions.assertEquals(testEvent, record.value());
    }
}