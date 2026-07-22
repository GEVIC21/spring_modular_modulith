package com.monolith.modularmonolith.users.api;

/**
 * Port : Interface exposée par le module users
 * Permet au module auth de vérifier les mots de passe sans accéder au password en clair
 *
 * ✅ SÉCURITE: Le mot de passe reste hashé et n'est jamais exposé directement
 */
public interface UserPasswordVerifier {

    /**
     * Vérifie si le mot de passe en clair correspond au hash stocké pour l'utilisateur
     *
     * @param email l'email de l'utilisateur
     * @param rawPassword le mot de passe en clair
     * @return true si le mot de passe est correct
     */
    boolean verifyPassword(String email, String rawPassword);

    /**
     * Vérifie si le mot de passe en clair correspond au hash stocké pour l'utilisateur
     *
     * @param userId l'ID de l'utilisateur
     * @param rawPassword le mot de passe en clair
     * @return true si le mot de passe est correct
     */
    boolean verifyPasswordById(Long userId, String rawPassword);
}