package com.poc.microservices.main.app.config.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JKSFileUtil {
    private static final Logger logger = LoggerFactory.getLogger(JKSFileUtil.class);

    public static File loadTempJKS(String prefix, String classpathRelativePath) {
        try {
            File tempFile = File.createTempFile(prefix, ".jks");
            try (InputStream inputStream = JKSFileUtil.class.getClassLoader().getResourceAsStream(classpathRelativePath)) {
                if (inputStream == null) {
                    logger.warn("Input stream is null for {}", classpathRelativePath);
                    return null;
                }
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                logger.info("Created temp file: {}", tempFile.getAbsolutePath());
                return tempFile;
            } catch (Exception e) {
                logger.error("Error reading classpath resource {}", classpathRelativePath, e);
            }
        } catch (IOException ioe) {
            logger.error("Error creating temp file for prefix {}", prefix, ioe);
        }
        return null;
    }
}