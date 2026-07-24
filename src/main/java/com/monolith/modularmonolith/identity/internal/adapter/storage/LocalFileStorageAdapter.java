package com.monolith.modularmonolith.identity.internal.adapter.storage;

import com.monolith.modularmonolith.identity.internal.application.port.outbound.FileStorage;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import com.monolith.modularmonolith.shared.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class LocalFileStorageAdapter implements FileStorage {

    private final Path storageLocation;
    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public LocalFileStorageAdapter(
            @Value("${app.storage.upload-dir:uploads/avatars}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            log.error("Impossible de créer le répertoire: {}", storageLocation, e);
            throw new ValidationException(ErrorCode.STORAGE_001,
                    "Impossible d'initialiser le stockage");
        }
    }

    @Override
    public String store(MultipartFile file, String directory) {
        validateFile(file);
        String original = file.getOriginalFilename();
        String safeName = (original != null) ? original.replaceAll("[^a-zA-Z0-9.-]", "_") : "file";
        String filename = UUID.randomUUID() + "_" + safeName;

        Path targetDir = storageLocation.resolve(directory).normalize();
        Path targetPath = targetDir.resolve(filename);

        try {
            Files.createDirectories(targetDir);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Fichier stocké: {}", targetPath);
            return directory + "/" + filename;
        } catch (IOException e) {
            log.error("Erreur stockage: {}", filename, e);
            throw new ValidationException(ErrorCode.STORAGE_001, "Erreur stockage fichier");
        }
    }

    @Override
    public void delete(String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            Path filePath = resolvePath(filename);
            Files.deleteIfExists(filePath);
            // Supprime aussi la thumbnail si elle existe
            Path thumbPath = resolvePath("thumb_" + filename);
            Files.deleteIfExists(thumbPath);
            log.info("Fichier(s) supprimé(s): {}", filename);
        } catch (IOException e) {
            log.error("Erreur suppression: {}", filename, e);
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path filePath = resolvePath(filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("Fichier", filename);
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Fichier", filename);
        }
    }

    @Override
    public String getUrl(String filename) {
        if (filename == null || filename.isBlank()) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/public/avatars/")
                .path(filename)
                .toUriString();
    }

    @Override
    public String getFileUrl(String filename) {
        return getUrl(filename);
    }

    @Override
    public String getThumbnailUrl(String filename) {
        if (filename == null || filename.isBlank()) return null;
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/public/avatars/thumb/")
                .path(filename)
                .toUriString();
    }

    @Override
    public String resolveContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException(ErrorCode.VALID_001, "Fichier requis");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ValidationException(ErrorCode.VALID_003, "Fichier trop volumineux (max 5 Mo)");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ValidationException(ErrorCode.VALID_002, "Type non supporté (JPEG, PNG, GIF, WEBP)");
        }
    }

    private Path resolvePath(String filename) {
        Path target = storageLocation.resolve(filename).normalize();
        if (!target.startsWith(storageLocation)) {
            throw new ValidationException(ErrorCode.VALID_001, "Chemin invalide");
        }
        return target;
    }
}