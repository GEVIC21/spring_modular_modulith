package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // <-- Indique à Spring que c'est le Bean UserDetailsService officiel
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Injection du repository pour requêter la base de données
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Recherche l'utilisateur par son email (ou username) dans la base de données
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + username));
    }
}
