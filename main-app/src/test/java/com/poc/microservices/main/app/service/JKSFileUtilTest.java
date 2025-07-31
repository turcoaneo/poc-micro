package com.poc.microservices.main.app.service;

import com.poc.microservices.main.app.config.helper.JKSFileUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class JKSFileUtilTest {

    @Test
    void shouldLoadJKSFileFromClasspath() {
        File jksFile = JKSFileUtil.loadTempJKS("testKeystore", "kafka/test-client.jks");
        Assertions.assertNotNull(jksFile);
        Assertions.assertTrue(jksFile.exists());
        Assertions.assertTrue(jksFile.length() > 0);
    }

    @Test
    void shouldReturnNullIfResourceMissing() {
        File jksFile = JKSFileUtil.loadTempJKS("missingKeystore", "nonexistent/file.jks");
        Assertions.assertNull(jksFile);
    }

    @Test
    void shouldCreateFileWithJksExtension() {
        File jksFile = JKSFileUtil.loadTempJKS("extTest", "kafka/test-client.jks");
        Assertions.assertNotNull(jksFile);
        Assertions.assertTrue(jksFile.getName().endsWith(".jks"));
    }
}