package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import com.monolith.modularmonolith.users.internal.model.SchoolUser;
import com.monolith.modularmonolith.users.internal.repository.SchoolUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserLookupImpl implements UserLookup {

    private final SchoolUserRepository schoolUserRepository;

    public UserLookupImpl(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @Override
    public Optional<UserSummary> findById(Long id) {
        return schoolUserRepository.findById(id)
                .map(this::toUserSummary);
    }

    @Override
    public List<UserSummary> findAllById(Set<Long> ids) {
        return schoolUserRepository.findAllById(ids).stream()
                .map(this::toUserSummary)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserSummary> findByEmail(String email) {
        return schoolUserRepository.findByEmail(email)
                .map(this::toUserSummary);
    }

    @Override
    public boolean existsById(Long id) {
        return schoolUserRepository.existsById(id);
    }

    private UserSummary toUserSummary(SchoolUser user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles() != null ? Set.copyOf(user.getRoles()) : Set.of()
        );
    }
}