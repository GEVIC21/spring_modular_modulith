package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserPasswordVerifier;
import com.monolith.modularmonolith.users.internal.repository.SchoolUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordVerifierImpl implements UserPasswordVerifier {

    private final SchoolUserRepository schoolUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserPasswordVerifierImpl(
            SchoolUserRepository schoolUserRepository,
            PasswordEncoder passwordEncoder) {
        this.schoolUserRepository = schoolUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean verifyPassword(String email, String rawPassword) {
        return schoolUserRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    @Override
    public boolean verifyPasswordById(Long userId, String rawPassword) {
        return schoolUserRepository.findById(userId)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }
}