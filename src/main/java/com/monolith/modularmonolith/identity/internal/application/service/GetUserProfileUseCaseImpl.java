package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.GetUserProfileUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.response.UserProfileResponse;
import com.monolith.modularmonolith.identity.internal.mapper.UserProfileMapper;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserProfileUseCaseImpl implements GetUserProfileUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse byEmail(String email) {
        User user = userRepository.findByEmailWithProfiles(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.IDENTITY_001, "Utilisateur introuvable avec l'email : " + email));
        return userProfileMapper.toUserProfileResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse byId(Long userId) {
        User user = userRepository.findByIdWithProfiles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));
        return userProfileMapper.toUserProfileResponse(user);
    }
}