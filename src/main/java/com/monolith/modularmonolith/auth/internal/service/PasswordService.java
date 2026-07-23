package com.monolith.modularmonolith.auth.internal.service;

import com.monolith.modularmonolith.users.internal.model.SchoolUser;
import com.monolith.modularmonolith.users.internal.repository.SchoolUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final SchoolUserRepository schoolUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(String userEmail, String currentPassword, String newPassword, String confirmPassword) {
        validatePasswordChange(newPassword, confirmPassword);

        SchoolUser user = schoolUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException("Utilisateur non trouvé."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Le mot de passe actuel est incorrect.");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit être différent de l'ancien.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        schoolUserRepository.save(user);
    }

    private void validatePasswordChange(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Le nouveau mot de passe et la confirmation ne correspondent pas.");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }
    }
}