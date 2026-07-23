package com.monolith.modularmonolith.identity.internal.domain.model;

/**
 * Type de profil associé à un utilisateur.
 * Détermine quel sous-profil (Student, Teacher, Admin) est actif.
 */
public enum ProfileType {
    STUDENT,
    TEACHER,
    ADMIN,
    SUPER_ADMIN,
    PARENT
}