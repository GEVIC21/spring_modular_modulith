package com.monolith.modularmonolith.auth.internal.service;

import com.monolith.modularmonolith.auth.internal.adapter.UserDetailsAdapter;
import com.monolith.modularmonolith.users.api.UserSummary;
import com.monolith.modularmonolith.users.internal.model.SchoolUser;
import com.monolith.modularmonolith.users.internal.repository.SchoolUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridge entre le module users et Spring Security.
 *
 * ✅ Accède directement au repository pour récupérer le password hashé
 * ✅ Crée l'adapter avec le vrai hash pour l'AuthenticationManager
 * ✅ Utilise UserSummary pour le reste (découplage métier)
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final SchoolUserRepository schoolUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SchoolUser schoolUser = schoolUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé: " + email
                ));

        // DTO pour l'adapter (pas d'exposition directe de l'entité)
        UserSummary summary = new UserSummary(
                schoolUser.getId(),
                schoolUser.getUsername(),
                schoolUser.getEmail(),
                schoolUser.getRoles() != null ? java.util.Set.copyOf(schoolUser.getRoles()) : java.util.Set.of()
        );

        // Adapter avec le VRAI password hashé
        return new UserDetailsAdapter(summary, schoolUser.getPassword());
    }
}