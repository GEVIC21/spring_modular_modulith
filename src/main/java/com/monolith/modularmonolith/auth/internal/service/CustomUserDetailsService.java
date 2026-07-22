package com.monolith.modularmonolith.auth.internal.service;

import com.monolith.modularmonolith.auth.internal.adapter.UserDetailsAdapter;
import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Bridge entre le module users (port UserLookup) et Spring Security.
 *
 * ✅ Une seule responsabilité : charger un UserDetails depuis le port
 * ✅ Délégation complète au module users (pas de logique métier ici)
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserLookup userLookup;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserSummary user = userLookup.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé: " + email
                ));

        return new UserDetailsAdapter(user);
    }
}