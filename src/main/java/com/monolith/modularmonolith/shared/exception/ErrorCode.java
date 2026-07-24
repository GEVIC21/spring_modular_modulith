package com.monolith.modularmonolith.shared.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum centralisé de tous les codes d'erreur de l'application.
 * Format: MODULE-XXX (ex: IDENTITY-001, AUTH-001, VALID-001)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // === Génériques ===
    GEN_001("GEN-001", "Une erreur interne est survenue"),
    GEN_002("GEN-002", "Ressource introuvable"),

    // === Identity ===
    IDENTITY_001("IDENTITY-001", "Utilisateur introuvable"),
    IDENTITY_002("IDENTITY-002", "Email déjà utilisé"),
    IDENTITY_003("IDENTITY-003", "Nom d'utilisateur déjà utilisé"),
    IDENTITY_004("IDENTITY-004", "ID étudiant déjà existant"),
    IDENTITY_005("IDENTITY-005", "ID enseignant déjà existant"),
    IDENTITY_006("IDENTITY-006", "ID administrateur déjà existant"),
    IDENTITY_007("IDENTITY-007", "Utilisateur déjà désactivé"),
    IDENTITY_008("IDENTITY-008", "Utilisateur déjà actif"),
    IDENTITY_009("IDENTITY-009", "Aucun profil associé trouvé"),
    IDENTITY_010("IDENTITY-010", "Avatar introuvable"),

    // === Authentification ===
    AUTH_001("AUTH-001", "Identifiants invalides"),
    AUTH_002("AUTH-002", "Token JWT invalide ou expiré"),
    AUTH_003("AUTH-003", "Token manquant"),
    AUTH_004("AUTH-004", "Accès refusé - permissions insuffisantes"),
    AUTH_005("AUTH-005", "Ancien mot de passe incorrect"),
    AUTH_006("AUTH-006", "Les mots de passe ne correspondent pas"),

    // === Validation ===
    VALID_001("VALID-001", "Erreur de validation des données"),
    VALID_002("VALID-002", "Format de fichier non supporté"),
    VALID_003("VALID-003", "Fichier trop volumineux"),
    VALID_004("VALID-004", "Type MIME non autorisé"),

    // === Stockage ===
    STORAGE_001("STORAGE-001", "Erreur lors du stockage du fichier"),
    STORAGE_002("STORAGE-002", "Fichier introuvable sur le disque");

    private final String code;
    private final String defaultMessage;
}