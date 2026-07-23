package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.*;
import java.util.Set;

public record UpdateAdminProfileRequest(
        // === Champs base utilisateur ===
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String phone,

        // === Champs admin ===
        @Size(max = 100) String department,
        @Size(max = 100) String jobTitle,
        @Size(max = 50) String accessLevel,
        Set<@Size(max = 50) String> managedModules,
        Boolean canManageUsers,
        Boolean canManageFinances,
        Boolean canManageAcademics,
        @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{8,20}$") String officePhone,
        @Size(max = 100) String officeLocation
) {}