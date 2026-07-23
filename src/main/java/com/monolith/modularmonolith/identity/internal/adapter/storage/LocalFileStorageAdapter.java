package com.monolith.modularmonolith.identity.internal.adapter.storage;

import com.monolith.modularmonolith.identity.internal.application.port.outbound.FileStorage;
import com.monolith.modularmonolith.shared.exception.DomainException;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class LocalFileStorageAdapter implements FileStorage {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Value("${app.upload.directory}")
    private String uploadDir;

    private Path rootLocation;
    private final Tika tika = new Tika();

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
            log.info("Upload directory initialized: {}", this.rootLocation);
        } catch (IOException e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, "Could not create upload directory");
        }
    }

    @Override
    public String store(MultipartFile file, String identifier) {
        validateFile(file);

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(original);
        if (extension == null) extension = "png";

        String filename = identifier.replaceAll("[^a-zA-Z0-9]", "_")
                + "_" + UUID.randomUUID().toString().substring(0, 8)
                + "." + extension.toLowerCase();

        try {
            Files.copy(file.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, "Failed to store file");
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "File not found: " + filename);
        } catch (MalformedURLException e) {
            throw new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "Invalid file: " + filename);
        }
    }

    @Override
    public void delete(String filename) {
        if (filename == null) return;
        try {
            Files.deleteIfExists(rootLocation.resolve(filename));
            Files.deleteIfExists(rootLocation.resolve("thumb_" + filename));
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", filename);
        }
    }

    @Override
    public String resolveContentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".gif")) return "image/gif";
        if (filename.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    @Override
    public String getFileUrl(String filename) {
        return "/api/v1/public/avatars/" + filename;
    }

    @Override
    public String getThumbnailUrl(String filename) {
        return "/api/v1/public/avatars/thumb_" + filename;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "File is empty");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(original);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new DomainException(ErrorCode.INVALID_FILE_TYPE);
        }

        if (original.contains("..")) {
            throw new DomainException(ErrorCode.VALIDATION_ERROR, "Invalid filename");
        }

        try (InputStream is = file.getInputStream()) {
            String detected = tika.detect(is);
            if (!ALLOWED_MIME_TYPES.contains(detected)) {
                throw new DomainException(ErrorCode.INVALID_FILE_TYPE, "Detected: " + detected);
            }
        } catch (IOException e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR, "Could not verify file type");
        }
    }
}