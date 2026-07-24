package com.monolith.modularmonolith.identity.api.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserDeactivatedEvent(
        Long userId,
        String email,
        String reason,
        Instant occurredOn
) {}