package com.poc.microservices.main.app.config;

import com.poc.microservices.main.app.config.helper.JKSFileUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@ConditionalOnProperty(prefix = "kafka", name = "enabled", havingValue = "true")
public class KafkaConsumerConfig {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    @Value("${kafka.hostname}")
    private String hostname;
    @Value("${kafka.port}")
    private String port;
    @Value("${spring.profiles.active:local}")
    private String activeProfile;
    @Value("${kafka.security:ssl}")
    String security;
    @Value("${kafka.filePath:localDummy}")
    String filePath;
    @Value("${kafka.truststore:localDummy}")
    String truststore;

    public static final String GROUP_NAME = "mas-consumer";
    public static final String TOPIC = "eem.employees.linked";

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        logger.info("MAS Kafka bean for listening on {} is enabled on port {}", hostname, port);
        Map<String, Object> props = getKafkaProps();
        if (!"local".equals(activeProfile) && !"plaintext".equals(security)) {
            setKafkaSSLProps(props);
        }
        return new DefaultKafkaConsumerFactory<>(props);
    }

    private void setKafkaSSLProps(Map<String, Object> props) {
        String keyName = "JKS_KEY";
        String secretKey = System.getenv(keyName) != null ? System.getenv(keyName) : System.getProperty(keyName);
        props.put("security.protocol", "SSL");
        props.put("ssl.key.password", secretKey);

        String defaultFolder = "kafka/";
        String keyStoreFile = defaultFolder + filePath;
        String trustStoreFile = defaultFolder + truststore;

        File tempClient = getTempJKSFile("client", keyStoreFile);
        if (tempClient != null) {
            props.put("ssl.keystore.password", secretKey);
            props.put("ssl.keystore.location", tempClient.getAbsolutePath());
        }

        File tempTrustStore = getTempJKSFile("truststore", trustStoreFile);
        if (tempTrustStore != null) {
            props.put("ssl.truststore.password", secretKey);
            props.put("ssl.truststore.location", tempTrustStore.getAbsolutePath());
        }
    }

    private File getTempJKSFile(String tempFile, String filePath) {
        return JKSFileUtil.loadTempJKS(tempFile, filePath);
    }

    @NotNull
    private Map<String, Object> getKafkaProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hostname + ":" + port);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_NAME);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}