package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.PatchUserUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.Role;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.request.UserStatusUpdateRequest;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatchUserUseCaseImpl implements PatchUserUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper mapper;

    @Override
    public UserProfileResponse execute(Long userId, UserStatusUpdateRequest request) {
        User user = userRepository.findByIdWithProfiles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

        if (request.active() != null) user.setActive(request.active());
        if (request.emailVerified() != null) user.setEmailVerified(request.emailVerified());
        if (request.email() != null) user.setEmail(request.email());
        if (request.profileType() != null) user.setProfileType(request.profileType());
        if (request.roles() != null) user.setRoles(new HashSet<>(request.roles()));

        User saved = userRepository.save(user);
        log.info("User {} patched by admin", userId);
        return mapper.toUserProfileResponse(saved);
    }
}