package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.model.AdminProfile;
import com.monolith.modularmonolith.users.internal.model.SchoolUser;
import com.monolith.modularmonolith.users.internal.model.StudentProfile;
import com.monolith.modularmonolith.users.internal.model.TeacherProfile;
import com.monolith.modularmonolith.users.internal.repository.AdminProfileRepository;
import com.monolith.modularmonolith.users.internal.repository.SchoolUserRepository;
import com.monolith.modularmonolith.users.internal.repository.StudentProfileRepository;
import com.monolith.modularmonolith.users.internal.repository.TeacherProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Initialisateur des données de démonstration pour le système de gestion scolaire.
 * Crée les comptes par défaut : superadmin, admin, enseignant test, élève test.
 *
 * À désactiver en production ou remplacer par des migrations Flyway/Liquibase.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchoolDataInitializer implements CommandLineRunner {

    private final SchoolUserRepository schoolUserRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Vérifier si des données existent déjà
        if (schoolUserRepository.count() > 0) {
            log.info("Données utilisateurs déjà initialisées, skip.");
            return;
        }

        log.info("=== INITIALISATION SCHOOL MANAGEMENT SYSTEM ===");

        // ========== SUPERADMIN ==========
        SchoolUser superAdmin = createUser(
                "superadmin",
                "superadmin@ecole.fr",
                "SuperAdmin123!",
                Set.of("SUPER_ADMIN"),
                "SUPER_ADMIN"
        );
        log.info("SuperAdmin créé: superadmin@ecole.fr / SuperAdmin123!");

        // ========== ADMIN ==========
        SchoolUser admin = createUser(
                "admin",
                "admin@ecole.fr",
                "Admin123!",
                Set.of("ADMIN"),
                "ADMIN"
        );

        // Profil admin
        AdminProfile adminProfile = AdminProfile.builder()
                .user(admin)
                .adminId("ADM-00001")
                .department("Direction")
                .jobTitle("Administrateur Système")
                .hireDate(LocalDate.of(2020, 9, 1))
                .accessLevel("FULL")
                .canManageUsers(true)
                .canManageFinances(true)
                .canManageAcademics(true)
                .officeLocation("Bâtiment Principal, Bureau 001")
                .build();
        admin.setAdminProfile(adminProfile);
        schoolUserRepository.save(admin);
        log.info("Admin créé: admin@ecole.fr / Admin123!");

        // ========== ENSEIGNANT TEST ==========
        SchoolUser teacher = createUser(
                "prof_math",
                "prof.math@ecole.fr",
                "Prof123!",
                Set.of("TEACHER"),
                "TEACHER"
        );

        TeacherProfile teacherProfile = TeacherProfile.builder()
                .user(teacher)
                .teacherId("TCH-2026-00001")
                .employeeId("EMP-00001")
                .department("Sciences")
                .specialization("Mathématiques")
                .subjects(new HashSet<>(Set.of("Mathématiques", "Algèbre", "Géométrie")))
                .classesAssigned(new HashSet<>(Set.of("Terminale S1", "Terminale S2")))
                .hireDate(LocalDate.of(2020, 9, 1))
                .contractType("CDI")
                .qualification("Doctorat en Mathématiques Appliquées")
                .certifications("Agrégation de Mathématiques, CAPES")
                .phone("+33 6 11 22 33 44")
                .officeLocation("Bâtiment A, Bureau 105")
                .officeHours("Lundi 14h-16h, Mercredi 10h-12h")
                .bio("Professeur de mathématiques passionné avec 15 ans d'expérience. " +
                        "Spécialisé dans la préparation aux concours scientifiques.")
                .researchInterests("Analyse numérique, Probabilités")
                .yearsOfExperience(15)
                .tenured(true)
                .departmentHead(true)
                .build();
        teacher.setTeacherProfile(teacherProfile);
        schoolUserRepository.save(teacher);
        log.info("Enseignant test créé: prof.math@ecole.fr / Prof123!");

        // ========== ÉLÈVE TEST ==========
        SchoolUser student = createUser(
                "eleve_test",
                "eleve.test@ecole.fr",
                "Eleve123!",
                Set.of("STUDENT"),
                "STUDENT"
        );

        StudentProfile studentProfile = StudentProfile.builder()
                .user(student)
                .studentId("STD-2026-00001")
                .registrationNumber("REG-2026-00001")
                .gradeLevel("Terminale")
                .className("S2")
                .section("Scientifique")
                .academicYear("2025-2026")
                .placeOfBirth("Paris")
                .parentName("M. et Mme Dupont")
                .parentPhone("+33 6 55 66 77 88")
                .parentEmail("parents.dupont@email.fr")
                .emergencyContact("Mme Martin (tante)")
                .emergencyContactPhone("+33 6 99 88 77 66")
                .address("25 Avenue des Champs")
                .city("Paris")
                .postalCode("75008")
                .country("France")
                .enrollmentDate(LocalDate.of(2018, 9, 1))
                .graduationDate(LocalDate.of(2026, 7, 15))
                .scholarship(false)
                .gpa(15.5)
                .attendanceRate(95)
                .bloodGroup("A+")
                .allergies("Aucune allergie connue")
                .medicalNotes("Porte des lunettes")
                .extracurricularActivities(new HashSet<>(Set.of(
                        "Club de robotique",
                        "Équipe de basket",
                        "Orchestre scolaire"
                )))
                .build();
        student.setStudentProfile(studentProfile);
        schoolUserRepository.save(student);
        log.info("Élève test créé: eleve.test@ecole.fr / Eleve123!");

        log.info("=== INITIALISATION TERMINÉE ===");
        log.info("Comptes créés: {} utilisateurs", schoolUserRepository.count());
    }

    /**
     * Méthode utilitaire pour créer un utilisateur de base.
     */
    private SchoolUser createUser(String username, String email, String rawPassword,
                                  Set<String> roles, String profileType) {
        SchoolUser user = SchoolUser.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .active(true)
                .emailVerified(true)
                .roles(new HashSet<>(roles))
                .profileType(profileType)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return schoolUserRepository.save(user);
    }
}