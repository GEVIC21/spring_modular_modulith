package com.monolith.modularmonolith.users.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Port : Interface publique exposée par le module users
 *
 * Cette interface permet aux autres modules (auth, grades, attendance, etc.)
 * de rechercher les utilisateurs sans dépendre des détails d'implémentation
 *
 * ✅ Loose Coupling: Les autres modules injectent cette interface, pas UserRepository
 */
public interface UserLookup {

    /**
     * Recherche un utilisateur par son ID
     * @param id l'ID de l'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    Optional<UserSummary> findById(Long id);

    /**
     * Recherche un utilisateur par son email
     * @param email l'email de l'utilisateur
     * @return Optional contenant l'utilisateur ou vide si non trouvé
     */
    Optional<UserSummary> findByEmail(String email);

    /**
     * Recherche une liste d'utilisateurs par leurs IDs
     * @param ids l'ensemble des IDs à rechercher
     * @return liste des utilisateurs trouvés
     */
    List<UserSummary> findAllById(Set<Long> ids);

    /**
     * Vérifie qu'un utilisateur existe
     * @param id l'ID de l'utilisateur
     * @return true si l'utilisateur existe
     */
    boolean existsById(Long id);
}