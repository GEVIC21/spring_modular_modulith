package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.UpdateAvatarUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateAvatarUseCaseImpl implements UpdateAvatarUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(String email, String filename) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND));
        user.setAvatarUrl(filename);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}