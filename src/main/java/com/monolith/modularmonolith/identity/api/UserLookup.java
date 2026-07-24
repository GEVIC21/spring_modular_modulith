package com.monolith.modularmonolith.identity.api;

import com.monolith.modularmonolith.identity.internal.domain.model.User;

import java.util.Optional;

/**
 * API publique du module Identity pour la recherche d'utilisateurs.
 * Utilisable par les autres modules (ex: authentication).
 */
public interface UserLookup {

    Optional<UserSummary> findByEmail(String email);

    Optional<UserSummary> findById(Long id);

    boolean existsByEmail(String email);
}