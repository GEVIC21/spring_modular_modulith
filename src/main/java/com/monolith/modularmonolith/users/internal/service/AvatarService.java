package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.config.FileStorageProperties;
import com.monolith.modularmonolith.security.exception.FileStorageException;
import com.monolith.modularmonolith.security.exception.InvalidFileException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class AvatarService {

    private final Path uploadDir;
    private final long maxSize;
    private final List<String> allowedTypes;

    public AvatarService(FileStorageProperties fileStorageProperties) {
        this.uploadDir = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.maxSize = fileStorageProperties.getMaxSize();
        this.allowedTypes = fileStorageProperties.getAllowedTypes();

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException ex) {
            throw new FileStorageException("Impossible de créer le répertoire d'upload", ex);
        }
    }

    public String storeFile(MultipartFile file, String userEmail) {
        validateFile(file);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".png";

        String newFilename = "avatar_" + UUID.randomUUID() + extension;

        Path targetLocation = this.uploadDir.resolve(newFilename);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return newFilename;
        } catch (IOException ex) {
            throw new FileStorageException("Échec du stockage du fichier " + newFilename, ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.uploadDir.resolve(filename).normalize();

            if (!filePath.startsWith(this.uploadDir)) {
                throw new FileStorageException("Chemin de fichier non autorisé: " + filename);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("Fichier non trouvé: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Fichier non trouvé: " + filename, ex);
        }
    }

    public void deleteFile(String filename) {
        if (filename == null || filename.isBlank()) return;
        try {
            Path target = this.uploadDir.resolve(filename).normalize();
            if (!target.startsWith(this.uploadDir)) {
                throw new FileStorageException("Chemin de fichier non autorisé");
            }
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new FileStorageException("Échec de la suppression: " + filename, ex);
        }
    }

    public String getFileUrl(String filename) {
        return "/uploads/profiles/" + filename;
    }

    /**
     * Détecte le content-type selon l'extension du fichier.
     */
    public String resolveContentType(String filename) {
        if (filename == null) return MediaType.IMAGE_JPEG_VALUE;
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG_VALUE;
        if (lower.endsWith(".gif")) return MediaType.IMAGE_GIF_VALUE;
        if (lower.endsWith(".webp")) return "image/webp";
        return MediaType.IMAGE_JPEG_VALUE;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("Le fichier est vide.");
        }
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("Fichier trop volumineux. Max: " + (maxSize / 1024 / 1024) + "MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new InvalidFileException("Type non autorisé. Acceptés: " + allowedTypes);
        }
    }
}
