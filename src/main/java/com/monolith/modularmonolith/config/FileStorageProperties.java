package com.monolith.modularmonolith.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "app.file")
public class FileStorageProperties {
    private String uploadDir = "./uploads";
    private long maxSize = 5 * 1024 * 1024; // 5MB
    private List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
}