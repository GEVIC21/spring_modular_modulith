package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO pour la création d'un compte administrateur par un SUPER_ADMIN.
 */
public record AdminCreateRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 2, max = 100) String firstName,
        @NotBlank @Size(min = 2, max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String phone,

        // === Administratif ===
        @NotBlank @Size(max = 100) String department,
        @NotBlank @Size(max = 100) String jobTitle,
        @NotNull LocalDate hireDate,
        @Pattern(regexp = "^(FULL|LIMITED|READ_ONLY)$") String accessLevel,
        Set<@Size(max = 50) String> managedModules,
        boolean canManageUsers,
        boolean canManageFinances,
        boolean canManageAcademics,
        @Size(max = 50) String officePhone,
        @Size(max = 100) String officeLocation
) {}