package com.monolith.modularmonolith.identity.internal.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

/**
 * DTO de mise à jour du profil administrateur.
 * Tous les champs sont optionnels (PATCH sémantique).
 */
@Builder
public record UpdateAdminProfileRequest(
        @Size(min = 2, max = 100) String firstName,
        @Size(min = 2, max = 100) String lastName,
        @Size(max = 20) String phoneNumber,
        @Size(max = 100) String department,
        @Size(max = 20) String accessLevel,
        @Size(max = 1000) String permissions,
        LocalDate hireDate,
        @Size(max = 100) String jobTitle,
        @Size(max = 100) String officeLocation
) {
}