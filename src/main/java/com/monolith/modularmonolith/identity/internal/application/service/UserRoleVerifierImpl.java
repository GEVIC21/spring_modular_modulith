package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.UserRoleVerifier;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserRoleVerifierImpl implements UserRoleVerifier {

    private final UserRepository userRepository;

    @Override
    public boolean hasRole(Long userId, String role) {
        return userRepository.findById(userId)
                .map(u -> u.hasRole(Role.valueOf(role)))
                .orElse(false);
    }

    @Override
    public boolean hasAnyRole(Long userId, String... roles) {
        return userRepository.findById(userId)
                .map(u -> Arrays.stream(roles)
                        .map(Role::valueOf)
                        .anyMatch(u::hasAnyRole))
                .orElse(false);
    }
}