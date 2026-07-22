package com.monolith.modularmonolith.users.internal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    private String directory = "${java.io.tmpdir}/uploads/avatars";
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif", "webp");
    private List<String> allowedMimeTypes = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private long maxFileSize = 5 * 1024 * 1024; // 5MB
    private int maxWidth = 1024;
    private int maxHeight = 1024;
    private int thumbnailSize = 150;
}