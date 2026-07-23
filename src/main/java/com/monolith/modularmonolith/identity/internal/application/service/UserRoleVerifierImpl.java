package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.UserRoleVerifier;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleVerifierImpl implements UserRoleVerifier {

    private final UserRepository userRepository;

    @Override
    public boolean hasRole(Long userId, String roleName) {
        return userRepository.findById(userId)
                .map(user -> user.hasRole(Role.valueOf(roleName)))
                .orElse(false);
    }

    @Override
    public boolean hasAnyRole(Long userId, Set<String> roleNames) {
        Set<Role> roles = roleNames.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
        return userRepository.findById(userId)
                .map(user -> user.hasAnyRole(roles))
                .orElse(false);
    }

    @Override
    public boolean hasRole(String email, String roleName) {
        return userRepository.findByEmail(email)
                .map(user -> user.hasRole(Role.valueOf(roleName)))
                .orElse(false);
    }
}