package com.monolith.modularmonolith.identity.internal.application.service;

import com.monolith.modularmonolith.identity.internal.domain.model.*;
import com.monolith.modularmonolith.identity.internal.domain.repository.AdminProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.StudentProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.TeacherProfileRepository;
import com.monolith.modularmonolith.identity.internal.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Initialisateur des données de démonstration.
 * Crée les comptes par défaut : superadmin, admin, enseignant test, élève test.
 *
 * À désactiver en production (via @Profile("!prod") ou propriété).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchoolDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Données utilisateurs déjà initialisées, skip.");
            return;
        }

        log.info("=== INITIALISATION DES COMPTES PAR DÉFAUT ===");

        // ========== SUPERADMIN ==========
        User superAdmin = createBaseUser(
                "superadmin",
                "superadmin@ecole.fr",
                "SuperAdmin123!",
                "Super",
                "Admin",
                Set.of(Role.SUPER_ADMIN),
                ProfileType.SUPER_ADMIN
        );
        userRepository.save(superAdmin);
        log.info("SuperAdmin créé: superadmin@ecole.fr / SuperAdmin123!");

        // ========== ADMIN ==========
        User admin = createBaseUser(
                "admin",
                "admin@ecole.fr",
                "Admin123!",
                "Jean",
                "Dupont",
                Set.of(Role.ADMIN),
                ProfileType.ADMIN
        );

        AdminProfile adminProfile = AdminProfile.builder()
                .user(admin)
                .adminId("ADM-00001")
                .department("Direction")
                .jobTitle("Administrateur Système")
                .hireDate(LocalDate.of(2020, 9, 1))
                .accessLevel("FULL")
                .permissions("MANAGE_USERS,MANAGE_FINANCES,MANAGE_ACADEMICS")
                .officeLocation("Bâtiment Principal, Bureau 001")
                .build();
        admin.setAdminProfile(adminProfile);
        userRepository.save(admin);
        log.info("Admin créé: admin@ecole.fr / Admin123!");

        // ========== ENSEIGNANT TEST ==========
        User teacher = createBaseUser(
                "prof_math",
                "prof.math@ecole.fr",
                "Prof123!",
                "Marie",
                "Curie",
                Set.of(Role.TEACHER),
                ProfileType.TEACHER
        );
        teacher.setPhoneNumber("+33 6 11 22 33 44");

        TeacherProfile teacherProfile = TeacherProfile.builder()
                .user(teacher)
                .teacherId("TCH-2026-00001")
                .employeeId("EMP-00001")
                .department("Sciences")
                .specialization("Mathématiques")
                .subjects(new ArrayList<>(List.of("Mathématiques", "Algèbre", "Géométrie")))
                .classesAssigned(new ArrayList<>(List.of("Terminale S1", "Terminale S2")))
                .hireDate(LocalDate.of(2020, 9, 1))
                .qualification("Doctorat en Mathématiques Appliquées, Agrégation")
                .officeLocation("Bâtiment A, Bureau 105")
                .officeHours("Lundi 14h-16h, Mercredi 10h-12h")
                .build();
        teacher.setTeacherProfile(teacherProfile);
        userRepository.save(teacher);
        log.info("Enseignant test créé: prof.math@ecole.fr / Prof123!");

        // ========== ÉLÈVE TEST ==========
        User student = createBaseUser(
                "eleve_test",
                "eleve.test@ecole.fr",
                "Eleve123!",
                "Lucas",
                "Martin",
                Set.of(Role.STUDENT),
                ProfileType.STUDENT
        );

        StudentProfile studentProfile = StudentProfile.builder()
                .user(student)
                .studentId("STD-2026-00001")
                .gradeLevel("Terminale")
                .className("S2")
                .section("Scientifique")
                .academicYear("2025-2026")
                .enrollmentDate(LocalDate.of(2018, 9, 1))
                .scholarship(false)
                .parentName("M. et Mme Martin")
                .parentPhone("+33 6 55 66 77 88")
                .parentEmail("parents.martin@email.fr")
                .emergencyContact("Mme Bernard (tante)")
                .emergencyPhone("+33 6 99 88 77 66")
                .address("25 Avenue des Champs")
                .city("Paris")
                .postalCode("75008")
                .country("France")
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
        userRepository.save(student);
        log.info("Élève test créé: eleve.test@ecole.fr / Eleve123!");

        log.info("=== INITIALISATION TERMINÉE : {} utilisateurs créés ===", userRepository.count());
    }

    private User createBaseUser(String username, String email, String rawPassword,
                                String firstName, String lastName,
                                Set<Role> roles, ProfileType profileType) {
        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .firstName(firstName)
                .lastName(lastName)
                .active(true)
                .emailVerified(true)
                .profileType(profileType)
                .roles(new HashSet<>(roles))
                .build();
    }
}