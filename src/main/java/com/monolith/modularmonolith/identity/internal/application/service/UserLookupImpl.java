package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.UserLookup;
import com.monolith.modularmonolith.identity.api.UserSecurityInfo;
import com.monolith.modularmonolith.identity.api.UserSummary;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserLookupImpl implements UserLookup {

    private final UserRepository userRepository;
    private final UserProfileMapper mapper;

    @Override
    public Optional<UserSummary> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(mapper::toUserSummaryResponse)
                .map(r -> UserSummary.builder()
                        .id(r.id())
                        .email(r.email())
                        .username(r.username())
                        .fullName(r.fullName())
                        .profileType(r.profileType())
                        // Si r.roles() est null, on passe un Set vide à la place
                        .roles(r.roles() != null ? r.roles() : java.util.Set.of())
                        .active(r.active())
                        .build());
    }

    @Override
    public Optional<UserSummary> findById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toUserSummaryResponse)
                .map(r -> UserSummary.builder()
                        .id(r.id())
                        .email(r.email())
                        .username(r.username())
                        .fullName(r.fullName())
                        .profileType(r.profileType())
                        // Même sécurité ici
                        .roles(r.roles() != null ? r.roles() : java.util.Set.of())
                        .active(r.active())
                        .build());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserSecurityInfo> findSecurityInfoByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(u -> UserSecurityInfo.builder()
                        .email(u.getEmail())
                        .password(u.getPassword()) // ← le VRAI hash BCrypt
                        .roles(u.getRoles())
                        .active(u.isActive())
                        .build());
    }
}