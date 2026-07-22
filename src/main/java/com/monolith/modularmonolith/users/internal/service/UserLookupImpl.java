package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implémentation du port UserLookup
 * Classe INTERNE : les autres modules ne doivent pas dépendre de cette classe
 * Ils doivent passer par l'interface UserLookup dans le package api
 */
@Service
public class UserLookupImpl implements UserLookup {

    private final UserRepository userRepository;

    public UserLookupImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserSummary> findById(Long id) {
        return userRepository.findById(id)
                .map(this::toUserSummary);
    }

    @Override
    public List<UserSummary> findAllById(Set<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserSummary> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::toUserSummary);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Convertit une entité User en DTO UserSummary
     * N'expose que les données nécessaires aux autres modules
     */
    private UserSummary toUserSummary(User user) {
        Set<String> roles = user.getRoles() != null
                ? user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet())
                : Set.of();

        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );
    }
}