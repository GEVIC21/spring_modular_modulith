package com.monolith.modularmonolith.authentication.internal.application.service;

import com.monolith.modularmonolith.authentication.internal.application.port.inbound.ChangePasswordUseCase;
import com.monolith.modularmonolith.authentication.internal.dto.PasswordChangeRequest;
import com.monolith.modularmonolith.identity.api.UserPasswordVerifier;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import com.monolith.modularmonolith.shared.exception.ErrorCode;
import com.monolith.modularmonolith.shared.exception.UnauthorizedException;
import com.monolith.modularmonolith.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final UserPasswordVerifier passwordVerifier;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void execute(String email, PasswordChangeRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ValidationException(ErrorCode.AUTH_006, "Les mots de passe ne correspondent pas");
        }

        if (!passwordVerifier.verifyPassword(email, request.currentPassword())) {
            throw new UnauthorizedException("Ancien mot de passe incorrect");
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed for {}", email);
    }
}