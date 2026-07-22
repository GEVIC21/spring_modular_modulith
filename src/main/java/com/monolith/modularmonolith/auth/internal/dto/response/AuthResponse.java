package com.monolith.modularmonolith.auth.internal.dto.response;

import java.util.Set;

/**
 * Réponse d'authentification
 * ✅ DTO contenant uniquement les données nécessaires
 */
public record AuthResponse(
        String token,
        String email,
        String username,
        Set<String> roles
) {}