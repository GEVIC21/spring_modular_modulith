package com.monolith.modularmonolith.auth.internal.service;

import com.monolith.modularmonolith.auth.api.exception.AuthenticationFailedException;
import com.monolith.modularmonolith.auth.internal.adapter.UserDetailsAdapter;
import com.monolith.modularmonolith.auth.internal.dto.request.LoginRequest;
import com.monolith.modularmonolith.auth.internal.dto.response.AuthResponse;
import com.monolith.modularmonolith.security.jwt.JwtUtils;
import com.monolith.modularmonolith.users.api.UserLookup;
import com.monolith.modularmonolith.users.api.UserSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service d'authentification.
 *
 * ✅ Délégation complète à Spring Security (AuthenticationManager utilise CustomUserDetailsService)
 * ✅ Pas de redondance avec UserPasswordVerifier (l'AuthenticationManager gère déjà la vérification)
 * ✅ Exception unchecked propre
 * ✅ Pas de dépendance directe à UserPasswordVerifier (inutile ici)
 */
@Slf4j
@Service
@RequiredArgsConstructor
class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserLookup userLookup;

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // 1. Spring Security authentifie (vérifie email + password via CustomUserDetailsService)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // 2. Récupérer l'utilisateur via le port
            UserSummary user = userLookup.findByEmail(request.email())
                    .orElseThrow(() -> new AuthenticationFailedException("Utilisateur introuvable après authentification"));

            // 3. Adapter pour JwtUtils
            UserDetailsAdapter userDetails = new UserDetailsAdapter(user);

            // 4. Générer le token
            String token = jwtUtils.generateToken(userDetails);

            log.info("Authentification réussie pour {}", request.email());

            return new AuthResponse(
                    token,
                    user.email(),
                    user.username(),
                    user.roles()
            );

        } catch (BadCredentialsException ex) {
            log.warn("Échec d'authentification pour {} : mauvais identifiants", request.email());
            throw new AuthenticationFailedException("Email ou mot de passe invalide");
        }
    }
}