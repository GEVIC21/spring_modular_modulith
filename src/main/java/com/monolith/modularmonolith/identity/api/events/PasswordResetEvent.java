package com.monolith.modularmonolith.identity.api.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PasswordResetEvent(
        Long userId,
        String email,
        String temporaryPassword,
        Instant occurredOn
) {}