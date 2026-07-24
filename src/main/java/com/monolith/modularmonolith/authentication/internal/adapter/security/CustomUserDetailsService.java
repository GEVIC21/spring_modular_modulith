package com.monolith.modularmonolith.authentication.internal.adapter.security;

import com.monolith.modularmonolith.identity.api.UserLookup;
import com.monolith.modularmonolith.identity.api.UserSummary;
import com.monolith.modularmonolith.shared.exception.ResourceNotFoundException;
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
        UserSummary user = userLookup.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));

        var authorities = user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.email(),
                "", // Le password est chargé séparément par le AuthManager via le provider
                user.active(),
                true,
                true,
                true,
                authorities
        );
    }
}