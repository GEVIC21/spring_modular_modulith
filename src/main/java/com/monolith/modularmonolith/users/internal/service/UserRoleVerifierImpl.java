package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserRoleVerifier;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserRoleVerifierImpl implements UserRoleVerifier {

    private final UserRepository userRepository;

    @Override
    public boolean hasRole(Long userId, String roleName) {
        return userRepository.findById(userId)
                .map(u -> u.hasRole(roleName))
                .orElse(false);
    }

    @Override
    public boolean hasAnyRole(Long userId, Set<String> roleNames) {
        return userRepository.findById(userId)
                .map(u -> u.getRoleNames().stream().anyMatch(roleNames::contains))
                .orElse(false);
    }
}