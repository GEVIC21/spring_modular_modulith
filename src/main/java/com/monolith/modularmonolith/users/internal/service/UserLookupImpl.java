package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserLookupImpl implements UserLookup {

    private final UserRepository userRepository;

    @Override
    public Optional<UserSummary> findById(Long id) {
        return userRepository.findById(id).map(this::toSummary);
    }

    @Override
    public List<UserSummary> findAllById(Set<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public Optional<UserSummary> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toSummary);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    private UserSummary toSummary(com.monolith.modularmonolith.users.internal.model.User user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoleNames()
        );
    }
}