package com.monolith.modularmonolith.identity.api;

import java.util.Optional;

public interface UserLookup {

    Optional<UserSummary> findByEmail(String email);

    Optional<UserSummary> findById(Long id);

    boolean existsByEmail(String email);

    /**
     * Charge les infos de sécurité (inclut le mot de passe hashé).
     * Réservé à l'authentification Spring Security.
     */
    Optional<UserSecurityInfo> findSecurityInfoByEmail(String email);
}