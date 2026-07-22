package com.monolith.modularmonolith.users.api;

import java.util.Set;

/**
 * Port : Interface publique exposée par le module users
 *
 * Cette interface permet aux autres modules de vérifier les rôles des utilisateurs
 * sans dépendre de l'implémentation interne
 *
 * Utilisée par :
 * - auth (vérification des permissions)
 * - grades (vérifier que l'utilisateur est un étudiant/professeur)
 * - attendance (vérifier les permissions)
 *
 * ✅ Loose Coupling: Les autres modules injectent cette interface, pas UserRepository
 */
public interface UserRoleVerifier {

    /**
     * Vérifie si un utilisateur a un rôle spécifique
     * @param userId l'ID de l'utilisateur
     * @param roleName le nom du rôle (ex: "ADMIN", "TEACHER", "STUDENT")
     * @return true si l'utilisateur a ce rôle
     */
    boolean hasRole(Long userId, String roleName);

    /**
     * Vérifie si un utilisateur a l'un des rôles spécifiés
     * @param userId l'ID de l'utilisateur
     * @param roleNames l'ensemble des rôles à vérifier
     * @return true si l'utilisateur a au moins un des rôles
     */
    boolean hasAnyRole(Long userId, Set<String> roleNames);
}