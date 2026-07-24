package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.api.UserPasswordVerifier;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPasswordVerifierImpl implements UserPasswordVerifier {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean verifyPassword(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .map(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(false);
    }
}