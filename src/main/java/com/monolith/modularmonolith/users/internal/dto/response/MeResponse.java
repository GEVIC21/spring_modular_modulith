package com.monolith.modularmonolith.users.internal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO unifié pour le profil de l'utilisateur connecté (/api/v1/users/me).
 * Contient les champs communs à tous les rôles + des blocs optionnels
 * spécifiques selon le profil (student / teacher / admin).
 *
 * Les champs null sont exclus de la réponse JSON pour alléger la payload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MeResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String displayName,
        boolean active,
        boolean emailVerified,
        Set<String> roles,
        Set<String> permissions,
        String profileType,          // "STUDENT", "TEACHER", "ADMIN", "SUPER_ADMIN", "PARENT"
        String avatarUrl,
        String thumbnailUrl,
        String phone,
        String gender,               // "MALE", "FEMALE", "OTHER"
        LocalDate birthDate,
        String nationality,
        String language,             // Langue préférée
        String timezone,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        // === Bloc spécifique ÉLÈVE ===
        StudentInfo student,

        // === Bloc spécifique ENSEIGNANT ===
        TeacherInfo teacher,

        // === Bloc spécifique PARENT ===
        ParentInfo parent,

        // === Bloc spécifique ADMIN ===
        AdminInfo admin
) {

    public record StudentInfo(
            String studentId,              // Numéro matricule interne
            String registrationNumber,     // Numéro d'inscription
            String gradeLevel,             // Niveau scolaire (ex: "Terminale")
            String className,              // Nom de la classe (ex: "A", "B")
            String section,                // Section (ex: "Scientifique", "Littéraire")
            String academicYear,           // Année académique (ex: "2025-2026")
            LocalDate birthDate,
            String placeOfBirth,
            String parentName,
            String parentPhone,
            String parentEmail,
            String emergencyContact,
            String emergencyContactPhone,
            String address,
            String city,
            String postalCode,
            String country,
            LocalDate enrollmentDate,
            LocalDate graduationDate,
            boolean scholarship,
            String scholarshipType,        // Type de bourse
            double gpa,                    // Moyenne générale
            int attendanceRate,            // Taux de présence (%)
            String bloodGroup,             // Groupe sanguin
            String allergies,              // Allergies médicales
            String medicalNotes,           // Notes médicales
            Set<String> extracurricularActivities  // Activités extrascolaires
    ) {}

    public record TeacherInfo(
            String teacherId,              // Numéro matricule enseignant
            String employeeId,             // Numéro employé RH
            String department,             // Département (ex: "Mathématiques")
            String specialization,         // Spécialisation
            Set<String> subjects,          // Matières enseignées
            Set<String> classesAssigned,   // Classes assignées
            LocalDate hireDate,
            LocalDate contractEndDate,
            String contractType,           // "CDI", "CDD", "VACATAIRE"
            String qualification,          // Diplôme (ex: "Master", "Doctorat")
            String certifications,         // Certifications professionnelles
            String phone,
            String officeLocation,         // Localisation du bureau
            String officeHours,            // Heures de permanence
            String bio,                    // Biographie professionnelle
            String researchInterests,      // Domaines de recherche
            int yearsOfExperience,
            boolean tenured,               // Titulaire
            boolean departmentHead,        // Chef de département
            double salary,                 // Salaire (si autorisé)
            String bankAccount,            // RIB (chiffré en DB)
            String socialSecurityNumber    // Numéro sécurité sociale
    ) {}

    public record ParentInfo(
            String parentId,
            String relationship,           // "PÈRE", "MÈRE", "TUTEUR"
            Set<Long> childrenIds,         // IDs des enfants inscrits
            String profession,
            String workplace,
            String workPhone,
            boolean isEmergencyContact,
            boolean isBillingContact
    ) {}

    public record AdminInfo(
            String adminId,
            String department,             // Service administratif
            String jobTitle,               // Intitulé du poste
            LocalDate hireDate,
            String accessLevel,            // Niveau d'accès au système
            Set<String> managedModules,    // Modules gérés
            boolean canManageUsers,
            boolean canManageFinances,
            boolean canManageAcademics,
            String officePhone,
            String officeLocation
    ) {}
}