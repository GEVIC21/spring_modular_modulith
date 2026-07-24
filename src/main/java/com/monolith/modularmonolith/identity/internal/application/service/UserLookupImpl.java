package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.UserLookup;
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
                        .roles(r.roles())
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
                        .roles(r.roles())
                        .active(r.active())
                        .build());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}