package com.poc.microservices.employee.app.kafka;


import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


@Getter
@Service
public class KafkaEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaProducer<String, String> producer;
    private static final String TOPIC = "eem.employees.linked";

    public KafkaEventPublisher(@Value("${kafka.hostname}") String hostname,
                               @Value("${kafka.port}") String port,
                               @Value("${kafka.enabled}") String enabled,
                               @Value("${kafka.createTopic:false}") String shouldCreateTopic,
                               @Value("${kafka.security:ssl}") String security,
                               @Value("${jks.client.filePath}") String filePath,
                               @Value("${jks.client.truststore}") String truststore,
                               @Value("${spring.profiles.active:local}") String activeProfile) {
        KafkaProducer<String, String> tempProducer;
        if (Boolean.parseBoolean(enabled)) {
            Properties props = getKafkaProperties(hostname, port);

            if (!"local".equals(activeProfile) && !"plaintext".equals(security)) {
                setKafkaAdditionalProps(props, filePath, truststore);
            }
            try {
                logger.info("Trying to create EEM Kafka producer...");
                tempProducer = new KafkaProducer<>(props);
                List<PartitionInfo> result = tempProducer.partitionsFor(TOPIC);
                logger.info("Partition info result: {}", result);
                if (Boolean.parseBoolean(shouldCreateTopic)) createKafkaTopic(props, hostname, port);
            } catch (Exception e) {
                tempProducer = null;
                logger.error("Error during Kafka producer initialization", e);
            }
        } else {
            logger.warn("Kafka may be disabled, [kafka.enabled] property is actually {}", enabled);
            tempProducer = null;
        }
        this.producer = tempProducer;
    }

    private void createKafkaTopic(Properties props, String hostname, String port) {
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, hostname + ":" + port);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "5000");
        props.put(AdminClientConfig.CLIENT_ID_CONFIG, "java-admin-client");
        int retries = 1;
        int delay = 3000; // ms
        logger.info("Trying to create EEM Kafka topic...");
        for (int i = 0; i < retries; i++) {
            try (AdminClient admin = AdminClient.create(props)) {
                NewTopic topic = new NewTopic(TOPIC, 1, (short) 1);
                admin.createTopics(Collections.singleton(topic)).all().get();
                logger.info("Topic created: {}", TOPIC);
                break;
            } catch (Exception e) {
                logger.warn("Attempt {} failed to create topic: {}", i + 1, e.getMessage());
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    logger.error("Something crashed the delay", ie);
                }
            }
        }
    }

    @NotNull
    private static Properties getKafkaProperties(String hostname, String port) {
        Properties props = new Properties();
        props.put("bootstrap.servers", hostname + ":" + port); // Kafka broker
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    private void setKafkaAdditionalProps(Properties props, String filePath, String truststore) {
        props.put("retries", "1");
        props.put("request.timeout.ms", "10000");
        props.put("max.block.ms", "15000");
        props.put("buffer.memory", "33554432"); // 32MB
        String defaultFolder = "kafka/";
        String keyStoreFile = defaultFolder + filePath;
        String trustStoreFile = defaultFolder + truststore;

        File tempClient = getTempJKSFile("client", keyStoreFile);
        if (tempClient != null) props.put("ssl.keystore.location", tempClient.getAbsolutePath());

        File tempTrustStore = getTempJKSFile("truststore", trustStoreFile);
        if (tempTrustStore != null) props.put("ssl.truststore.location", tempTrustStore.getAbsolutePath());
    }

    private File getTempJKSFile(String tempFile, String filePath) {
        try {
            File temp = File.createTempFile(tempFile, ".jks");
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
                if (inputStream == null) return null;
                Files.copy(inputStream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Created temp {}", temp);
                return temp;
            }
        } catch (IOException e) {
            logger.error("Cannot copy input stream for {}", tempFile, e);
        }
        return null;
    }

    public void publishEvent(String key, String event) {
        if (producer == null) {
            logger.warn("Kafka producer is not initialized!");
            return;
        }
        producer.send(new ProducerRecord<>(TOPIC, key, event), (metadata, exception) -> {
            if (exception != null) {
                logger.error("Kafka send failed", exception);
            } else {
                logger.info("Sent to topic " + metadata.topic() + ", partition " + metadata.partition());
            }
        });
    }
}