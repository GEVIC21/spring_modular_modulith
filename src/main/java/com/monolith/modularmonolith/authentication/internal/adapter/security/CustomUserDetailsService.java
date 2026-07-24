package com.monolith.modularmonolith.authentication.internal.adapter.security;

import com.monolith.modularmonolith.identity.api.UserLookup;
import com.monolith.modularmonolith.identity.api.UserSecurityInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserLookup userLookup;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserSecurityInfo user = userLookup.findSecurityInfoByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + email));

        var authorities = user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.email(),
                user.password(), // ← VRAI mot de passe hashé, plus ""
                user.active(),
                true,
                true,
                true,
                authorities
        );
    }
}