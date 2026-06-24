package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.model.Role;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.RoleRepository;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cet e-mail est déjà utilisé.");
        }

        // Récupérer ou initialiser le rôle par défaut en base de données
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER", "Rôle utilisateur par défaut")));

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User newUser = new User(
                username,
                email,
                encodedPassword,
                new HashSet<>(Collections.singletonList(defaultRole))
        );

        return userRepository.save(newUser);
    }
}
