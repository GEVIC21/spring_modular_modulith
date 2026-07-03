package com.monolith.modularmonolith.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    public WebConfig(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = fileStorageProperties.getUploadDir();
        // S'assurer que le chemin se termine par /
        if (!uploadDir.endsWith("/")) {
            uploadDir = uploadDir + "/";
        }
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:" + uploadDir);
    }
}