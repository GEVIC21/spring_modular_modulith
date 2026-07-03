package com.monolith.modularmonolith.users.internal.dto.request;

import jakarta.validation.constraints.NotNull;

public record ActivateUserRequest(
        @NotNull(message = "L'ID utilisateur est obligatoire")
        Long userId,

        boolean active
) {}