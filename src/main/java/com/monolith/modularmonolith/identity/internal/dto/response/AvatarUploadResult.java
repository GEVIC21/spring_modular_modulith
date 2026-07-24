package com.monolith.modularmonolith.identity.internal.dto.response;

import lombok.Builder;

/**
 * DTO de résultat d'upload d'avatar.
 */
@Builder
public record AvatarUploadResult(
        String message,
        String filename,
        String avatarUrl,
        String thumbnailUrl
) {
}