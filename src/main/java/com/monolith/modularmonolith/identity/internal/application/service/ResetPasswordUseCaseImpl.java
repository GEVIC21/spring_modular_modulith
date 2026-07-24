package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.events.PasswordResetEvent;
import com.monolith.modularmonolith.identity.internal.application.port.inbound.ResetPasswordUseCase;
import com.monolith.modularmonolith.identity.internal.application.port.outbound.EventPublisher;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.response.PasswordResetResult;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;

    @Override
    public PasswordResetResult execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

        String temporaryPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        eventPublisher.publish(PasswordResetEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .temporaryPassword(temporaryPassword)
                .occurredOn(Instant.now())
                .build());

        log.info("Password reset for userId={}", userId);
        return PasswordResetResult.builder()
                .message("Mot de passe réinitialisé avec succès")
                .emailSent(true)
                .temporaryPassword(temporaryPassword)
                .build();
    }

    private String generateTemporaryPassword() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 12);
    }
}