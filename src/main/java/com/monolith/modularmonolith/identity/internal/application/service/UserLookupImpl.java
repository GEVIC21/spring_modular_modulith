package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.UserLookup;
import com.monolith.modularmonolith.identity.api.UserSummary;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLookupImpl implements UserLookup {

    private final UserRepository userRepository;

    @Override
    public Optional<UserSummary> findById(Long id) {
        return userRepository.findById(id).map(this::toSummary);
    }

    @Override
    public Optional<UserSummary> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toSummary);
    }

    @Override
    public List<UserSummary> findAllById(Set<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserSummary toSummary(User user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles() != null
                        ? user.getRoles().stream().map(Enum::name).collect(java.util.stream.Collectors.toSet())
                        : Set.of(),
                user.getProfileType() != null ? user.getProfileType().name() : null,
                user.isActive()
        );
    }
}