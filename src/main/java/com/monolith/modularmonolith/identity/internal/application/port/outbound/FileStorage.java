package com.monolith.modularmonolith.identity.internal.application.port.outbound;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Port sortant pour le stockage de fichiers (avatars, documents, etc.).
 */
public interface FileStorage {

    String store(MultipartFile file, String directory);

    void delete(String filename);

    Resource load(String filename);

    String getUrl(String filename);

    String getFileUrl(String filename);

    String getThumbnailUrl(String filename);

    String resolveContentType(String filename);
}