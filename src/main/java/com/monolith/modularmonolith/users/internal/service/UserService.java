package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.model.Role;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
        }

        // Hachage sécurisé du mot de passe avec le PasswordEncoder de SecurityConfig
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Attribution du rôle USER par défaut
        User newUser = new User(email, encodedPassword, Role.ROLE_USER);

        return userRepository.save(newUser);
    }
}
