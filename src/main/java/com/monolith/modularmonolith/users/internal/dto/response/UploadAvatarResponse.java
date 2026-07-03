package com.monolith.modularmonolith.users.internal.dto.response;

public record UploadAvatarResponse(
        String message,
        String avatarUrl
) {}