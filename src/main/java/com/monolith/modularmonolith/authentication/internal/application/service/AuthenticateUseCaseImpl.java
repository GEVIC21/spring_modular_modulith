package com.monolith.modularmonolith.authentication.internal.application.service;

import com.monolith.modularmonolith.authentication.internal.application.port.inbound.AuthenticateUseCase;
import com.monolith.modularmonolith.authentication.internal.dto.AuthResponse;
import com.monolith.modularmonolith.authentication.internal.dto.LoginRequest;
import com.monolith.modularmonolith.identity.api.UserLookup;
import com.monolith.modularmonolith.identity.api.UserSummary;
import com.monolith.modularmonolith.shared.exception.UnauthorizedException;
import com.monolith.modularmonolith.shared.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateUseCaseImpl implements AuthenticateUseCase {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserLookup userLookup;

    @Override
    public AuthResponse execute(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            UserSummary user = userLookup.findByEmail(request.email())
                    .orElseThrow(() -> new UnauthorizedException("Utilisateur introuvable après authentification"));

            String token = jwtUtils.generateToken(
                    new org.springframework.security.core.userdetails.User(
                            user.email(), "", user.roles().stream()
                            .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r))
                            .toList()
                    )
            );

            log.info("Authentication successful for {}", request.email());

            return new AuthResponse(token, user.email(), user.username(), user.roles());

        } catch (BadCredentialsException ex) {
            log.warn("Authentication failed for {}: bad credentials", request.email());
            throw new UnauthorizedException("Email ou mot de passe invalide");
        }
    }
}