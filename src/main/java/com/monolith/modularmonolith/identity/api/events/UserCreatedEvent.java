package com.monolith.modularmonolith.identity.api.events;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserCreatedEvent(
        Long userId,
        String email,
        String fullName,
        ProfileType profileType,
        Instant occurredOn
) {}