package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.model.Role;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Gère automatiquement l'injection par constructeur des champs final
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(username, email, encodedPassword, Role.ROLE_USER);

        return userRepository.save(newUser);
    }
}
