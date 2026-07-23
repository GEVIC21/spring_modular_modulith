package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.application.port.inbound.DeactivateUserUseCase;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivateUserUseCaseImpl implements DeactivateUserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));
        user.deactivate();
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("User deactivated: {}", userId);
    }
}