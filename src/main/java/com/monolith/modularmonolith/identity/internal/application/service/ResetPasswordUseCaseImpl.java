package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.events.PasswordResetEvent;
import com.monolith.modularmonolith.identity.internal.application.port.inbound.ResetPasswordUseCase;
import com.monolith.modularmonolith.identity.internal.application.port.outbound.EventPublisher;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.identity.internal.dto.response.PasswordResetResult;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PASSWORD_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public PasswordResetResult execute(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", userId));

        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password reset for user: {}", userId);

        eventPublisher.publish(new PasswordResetEvent(
                user.getId(), user.getEmail(), tempPassword, false, null
        ));

        return new PasswordResetResult(
                "Mot de passe réinitialisé avec succès",
                false,
                tempPassword
        );
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}