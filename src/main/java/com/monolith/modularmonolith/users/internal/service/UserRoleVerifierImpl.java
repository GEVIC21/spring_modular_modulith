package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserRoleVerifier;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Implémentation du port UserRoleVerifier
 * Classe INTERNE : les autres modules ne doivent pas dépendre de cette classe
 * Ils doivent passer par l'interface UserRoleVerifier dans le package api
 */
@Service
public class UserRoleVerifierImpl implements UserRoleVerifier {

    private final UserRepository userRepository;

    public UserRoleVerifierImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean hasRole(Long userId, String roleName) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase(roleName)))
                .orElse(false);
    }

    @Override
    public boolean hasAnyRole(Long userId, Set<String> roleNames) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().stream()
                        .map(role -> role.getName().toUpperCase())
                        .anyMatch(roleName -> roleNames.stream()
                                .anyMatch(r -> r.equalsIgnoreCase(roleName))))
                .orElse(false);
    }
}