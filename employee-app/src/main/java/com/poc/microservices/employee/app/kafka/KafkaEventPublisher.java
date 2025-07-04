package com.poc.microservices.employee.app.kafka;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class KafkaEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaProducer<String, String> producer;
    private static final String TOPIC = "eem.employees.linked";

    public KafkaEventPublisher() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092"); // Kafka broker
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);
    }

    public void publishEvent(String key, String event) {
        producer.send(new ProducerRecord<>(TOPIC, key, event));
        logger.info("Published event: " + event);
    }

    public void close() {
        producer.close();
    }
}