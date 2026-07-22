package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.config.UploadProperties;
import com.monolith.modularmonolith.users.internal.exception.FileNotFoundException;
import com.monolith.modularmonolith.users.internal.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private final UploadProperties uploadProperties;
    private final ImageProcessingService imageProcessingService;
    private final Tika tika = new Tika();

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadProperties.getDirectory())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Répertoire d'upload initialisé: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Impossible de créer le répertoire d'upload", ex);
        }
    }

    /**
     * Stocke un avatar avec validation complète (Magic Bytes, taille, dimensions).
     */
    public String storeAvatar(MultipartFile file, String userEmail) {
        validateFile(file);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFileName).toLowerCase();
        String storedFileName = generateUniqueFilename(userEmail, extension);

        try {
            // Redimensionnement et optimisation de l'image
            byte[] processedImage = imageProcessingService.processAvatar(
                    file.getInputStream(),
                    uploadProperties.getMaxWidth(),
                    uploadProperties.getMaxHeight()
            );

            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.write(targetLocation, processedImage);

            // Génération du thumbnail
            byte[] thumbnail = imageProcessingService.createThumbnail(
                    file.getInputStream(),
                    uploadProperties.getThumbnailSize()
            );
            String thumbnailName = "thumb_" + storedFileName;
            Path thumbnailLocation = this.fileStorageLocation.resolve(thumbnailName);
            Files.write(thumbnailLocation, thumbnail);

            log.info("Avatar stocké: {} pour {}", storedFileName, userEmail);
            return storedFileName;

        } catch (IOException ex) {
            throw new FileStorageException("Impossible de stocker le fichier " + originalFileName, ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new FileNotFoundException("Fichier non trouvé: " + fileName);
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("Fichier non trouvé: " + fileName, ex);
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) return;

        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);

            // Suppression du thumbnail associé
            Path thumbPath = this.fileStorageLocation.resolve("thumb_" + fileName).normalize();
            Files.deleteIfExists(thumbPath);

            log.info("Fichier supprimé: {}", fileName);
        } catch (IOException ex) {
            log.error("Erreur lors de la suppression du fichier: {}", fileName, ex);
        }
    }

    public String getFileUrl(String filename) {
        return "/api/v1/public/avatars/" + filename;
    }

    public String getThumbnailUrl(String filename) {
        return "/api/v1/public/avatars/thumb_" + filename;
    }

    public String resolveContentType(String filename) {
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".gif")) return "image/gif";
        if (filename.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    // ==================== VALIDATION ====================

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Le fichier est vide");
        }

        // Validation taille
        if (file.getSize() > uploadProperties.getMaxFileSize()) {
            throw new FileStorageException(
                    "Fichier trop volumineux. Max: " + (uploadProperties.getMaxFileSize() / 1024 / 1024) + "MB"
            );
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Validation path traversal
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Nom de fichier invalide: " + originalFileName);
        }

        // Validation extension
        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!uploadProperties.getAllowedExtensions().contains(extension)) {
            throw new FileStorageException("Type de fichier non autorisé: ." + extension);
        }

        // Validation Magic Bytes via Tika (vraie détection du type)
        try (InputStream is = file.getInputStream()) {
            String detectedType = tika.detect(is);
            if (!uploadProperties.getAllowedMimeTypes().contains(detectedType)) {
                throw new FileStorageException("Type de contenu non autorisé: " + detectedType);
            }
        } catch (IOException e) {
            throw new FileStorageException("Impossible de vérifier le type du fichier", e);
        }

        log.debug("Validation fichier OK: {} ({} bytes)", originalFileName, file.getSize());
    }

    private String generateUniqueFilename(String userEmail, String extension) {
        String sanitizedEmail = userEmail.replaceAll("[^a-zA-Z0-9]", "_");
        return sanitizedEmail + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex < 0) ? "" : fileName.substring(dotIndex + 1);
    }
}