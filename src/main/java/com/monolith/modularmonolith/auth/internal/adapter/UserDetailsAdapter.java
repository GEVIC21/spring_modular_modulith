package com.monolith.modularmonolith.auth.internal.adapter;

import com.monolith.modularmonolith.users.api.UserSummary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter : convertit UserSummary (DTO du module users) en UserDetails (Spring Security).
 *
 * ✅ Classe dédiée (pas d'inner class)
 * ✅ Record pour l'immutabilité
 * ✅ Préfixe ROLE_ géré ici (cohérence avec @PreAuthorize("hasRole('ADMIN')"))
 * ✅ getPassword() retourne une valeur valide (évite UnsupportedOperationException)
 */
public record UserDetailsAdapter(UserSummary user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        // Le mot de passe n'est PAS stocké dans UserSummary (sécurité).
        // Spring Security l'utilise via AuthenticationManager, pas via cet adapter.
        return "";
    }

    @Override
    public String getUsername() {
        return user.email();
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }

    // Accesseurs métier utiles pour les logs, audits, etc.
    public Long getUserId()     { return user.id(); }
    public String getEmail()    { return user.email(); }
    public Set<String> getRoles() { return user.roles(); }
}