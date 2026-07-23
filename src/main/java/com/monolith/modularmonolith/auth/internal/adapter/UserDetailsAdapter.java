package com.monolith.modularmonolith.auth.internal.adapter;

import com.monolith.modularmonolith.users.api.UserSummary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Adapter : convertit UserSummary (port users) en UserDetails (Spring Security).
 *
 * ✅ Stocke le password hashé pour l'AuthenticationManager
 * ✅ Ajoute le préfixe ROLE_ pour Spring Security
 */
public record UserDetailsAdapter(UserSummary user, String hashedPassword) implements UserDetails {

    /**
     * Constructeur sans password (pour compatibilité, ne pas utiliser pour l'auth)
     */
    public UserDetailsAdapter(UserSummary user) {
        this(user, "");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        return hashedPassword;  // ← VRAI hash BCrypt pour l'AuthenticationManager
    }

    @Override
    public String getUsername() {
        return user.email();
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }

    public Long getUserId()      { return user.id(); }
    public String getEmail()     { return user.email(); }
    public String getRawUsername() { return user.username(); }
}