package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserPasswordVerifier;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implémentation du port UserPasswordVerifier
 * Classe INTERNE : les autres modules ne doivent pas dépendre de cette classe
 * Ils doivent passer par l'interface UserPasswordVerifier dans le package api
 *
 * Avantage : Le mot de passe reste hashé et n'est jamais exposé aux autres modules
 */
@Service
public class UserPasswordVerifierImpl implements UserPasswordVerifier {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordVerifierImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean verifyPassword(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean verifyPasswordById(Long userId, String rawPassword) {
        return userRepository.findById(userId)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }
}