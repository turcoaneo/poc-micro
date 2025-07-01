package com.poc.microservices.main.app.service;

import com.poc.microservices.main.app.config.KafkaConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class KafkaEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventSubscriber.class);
    private final List<String> received = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "eem.employees.linked", groupId = KafkaConsumerConfig.GROUP_NAME)
    public void listen(String message) {
        logger.info("Received message: {}", message);
        received.add(message);
    }

    public List<String> getReceivedMessages() {
        return received;
    }
}