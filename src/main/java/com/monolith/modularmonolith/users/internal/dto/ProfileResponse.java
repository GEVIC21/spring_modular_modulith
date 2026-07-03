package com.monolith.modularmonolith.users.internal.dto;

import java.util.Set;

public record ProfileResponse(
        Long id,
        String username,
        String email,
        Set<String> roles,
        Set<String> permissions,
        String avatarUrl,      // Data URI SVG (défaut) ou URL/chemin fichier
        boolean hasCustomAvatar
) {}
