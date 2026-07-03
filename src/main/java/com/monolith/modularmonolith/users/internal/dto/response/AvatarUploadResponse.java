package com.monolith.modularmonolith.users.internal.dto.response;

public record AvatarUploadResponse(
        String message,
        String filename,
        String url
) {}