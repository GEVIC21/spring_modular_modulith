package com.monolith.modularmonolith.users.internal.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Value("${app.upload.dir:uploads/avatars}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() throws IOException {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(rootLocation);
    }

    public String store(MultipartFile file, String userIdentifier) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Type de fichier non autorisé. Formats acceptés : JPG, PNG, GIF, WEBP.");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(original);
        if (extension == null) extension = "png";

        String filename = userIdentifier + "_" + System.currentTimeMillis() + "." + extension.toLowerCase();

        try {
            Files.copy(file.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Échec du stockage du fichier.", e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("Fichier illisible ou introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Fichier invalide : " + filename, e);
        }
    }

    public void delete(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(rootLocation.resolve(filename));
        } catch (IOException e) {
            // Log silencieux, le fichier est peut-être déjà absent
        }
    }

    public String getContentType(String filename) {
        try {
            String type = Files.probeContentType(rootLocation.resolve(filename));
            return type != null ? type : "application/octet-stream";
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}