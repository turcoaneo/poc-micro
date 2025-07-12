package com.poc.microservices.main.app.service;

import com.poc.microservices.main.app.config.KafkaConsumerConfig;
import com.poc.microservices.main.app.service.util.KafkaMessageValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KafkaEventSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventSubscriber.class);
    private final List<String> received = new CopyOnWriteArrayList<>();

    private final KafkaMessageValidator validator;

    @KafkaListener(
            id = "eem-listener",
            topics = "eem.employees.linked",
            groupId = KafkaConsumerConfig.GROUP_NAME,
            autoStartup = "${kafka.enabled}"
    )
    public void listen(String message) {
        if (validator.isValid(message)) {
            logger.info("Valid message received: {}", message);
            received.add(message);
        } else {
            logger.warn("Invalid message discarded");
        }
    }

    public List<String> getReceivedMessages() {
        return received;
    }
}