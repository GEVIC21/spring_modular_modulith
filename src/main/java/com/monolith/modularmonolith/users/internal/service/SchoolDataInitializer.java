package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.model.*;
import com.monolith.modularmonolith.users.internal.repository.PermissionRepository;
import com.monolith.modularmonolith.users.internal.repository.RoleRepository;
import com.monolith.modularmonolith.users.internal.repository.StudentProfileRepository;
import com.monolith.modularmonolith.users.internal.repository.TeacherProfileRepository;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static com.monolith.modularmonolith.users.internal.model.SchoolConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchoolDataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentProfileRepository studentProfileRepository;
    private final TeacherProfileRepository teacherProfileRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            log.info("Données déjà initialisées, skip.");
            return;
        }

        log.info("=== INITIALISATION SCHOOL MANAGEMENT SYSTEM ===");

        // ========== PERMISSIONS (toutes dans une seule liste) ==========
        List<String[]> perms = Arrays.asList(
                new String[]{USER_CREATE, "Créer un utilisateur"},
                new String[]{USER_READ, "Lire un utilisateur"},
                new String[]{USER_UPDATE, "Modifier un utilisateur"},
                new String[]{USER_DELETE, "Supprimer un utilisateur"},

                new String[]{STUDENT_CREATE, "Créer un élève"},
                new String[]{STUDENT_READ, "Lire un élève"},
                new String[]{STUDENT_UPDATE, "Modifier un élève"},
                new String[]{STUDENT_DELETE, "Supprimer un élève"},
                new String[]{STUDENT_GRADES_READ, "Lire les notes"},
                new String[]{STUDENT_GRADES_WRITE, "Modifier les notes"},
                new String[]{STUDENT_ATTENDANCE_READ, "Lire l'assiduité"},
                new String[]{STUDENT_ATTENDANCE_WRITE, "Modifier l'assiduité"},

                new String[]{TEACHER_CREATE, "Créer un enseignant"},
                new String[]{TEACHER_READ, "Lire un enseignant"},
                new String[]{TEACHER_UPDATE, "Modifier un enseignant"},
                new String[]{TEACHER_DELETE, "Supprimer un enseignant"},
                new String[]{TEACHER_COURSES_MANAGE, "Gérer les cours"},
                new String[]{TEACHER_GRADES_MANAGE, "Gérer les notes"},

                new String[]{COURSE_CREATE, "Créer un cours"},
                new String[]{COURSE_READ, "Lire un cours"},
                new String[]{COURSE_UPDATE, "Modifier un cours"},
                new String[]{COURSE_DELETE, "Supprimer un cours"},

                new String[]{CLASS_MANAGE, "Gérer les classes"},

                new String[]{FINANCE_READ, "Lire les finances"},
                new String[]{FINANCE_WRITE, "Modifier les finances"},

                new String[]{REPORTS_READ, "Lire les rapports"},
                new String[]{REPORTS_WRITE, "Créer des rapports"},

                new String[]{SETTINGS_MANAGE, "Gérer les paramètres"},

                new String[]{ANNOUNCEMENT_CREATE, "Créer une annonce"},
                new String[]{ANNOUNCEMENT_READ, "Lire les annonces"},

                new String[]{PROFILE_READ, "Lire son profil"},
                new String[]{PROFILE_WRITE, "Modifier son profil"},

                // Permissions Emploi du temps (module Cours/Classes)
                new String[]{SCHEDULE_CREATE, "Créer un emploi du temps"},
                new String[]{SCHEDULE_READ, "Lire un emploi du temps"},
                new String[]{SCHEDULE_UPDATE, "Modifier un emploi du temps"},
                new String[]{SCHEDULE_DELETE, "Supprimer un emploi du temps"}
        );

        for (String[] p : perms) {
            createPermissionIfNotFound(p[0], p[1]);
        }

        // ========== RÔLES ==========

        // SUPERADMIN : TOUTES les permissions
        Role superAdminRole = createRole(ROLE_SUPERADMIN, "Super Administrateur - Accès total");
        superAdminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
        roleRepository.save(superAdminRole);

        // ADMIN : Gestion complète sauf superadmin
        Role adminRole = createRole(ROLE_ADMIN, "Administrateur de l'établissement");
        adminRole.setPermissions(new HashSet<>(List.of(
                perm(USER_CREATE), perm(USER_READ), perm(USER_UPDATE), perm(USER_DELETE),
                perm(STUDENT_CREATE), perm(STUDENT_READ), perm(STUDENT_UPDATE), perm(STUDENT_DELETE),
                perm(STUDENT_GRADES_READ), perm(STUDENT_GRADES_WRITE),
                perm(STUDENT_ATTENDANCE_READ), perm(STUDENT_ATTENDANCE_WRITE),
                perm(TEACHER_CREATE), perm(TEACHER_READ), perm(TEACHER_UPDATE), perm(TEACHER_DELETE),
                perm(TEACHER_COURSES_MANAGE), perm(TEACHER_GRADES_MANAGE),
                perm(COURSE_CREATE), perm(COURSE_READ), perm(COURSE_UPDATE), perm(COURSE_DELETE),
                perm(CLASS_MANAGE),
                perm(FINANCE_READ), perm(FINANCE_WRITE),
                perm(REPORTS_READ), perm(REPORTS_WRITE),
                perm(ANNOUNCEMENT_CREATE), perm(ANNOUNCEMENT_READ),
                perm(PROFILE_READ), perm(PROFILE_WRITE),
                perm(SCHEDULE_CREATE), perm(SCHEDULE_READ), perm(SCHEDULE_UPDATE), perm(SCHEDULE_DELETE)
        )));
        roleRepository.save(adminRole);

        // ENSEIGNANT : Cours, notes, assiduité, profil, emploi du temps (lecture)
        Role teacherRole = createRole(ROLE_ENSEIGNANT, "Enseignant");
        teacherRole.setPermissions(new HashSet<>(List.of(
                perm(STUDENT_READ),
                perm(STUDENT_GRADES_READ), perm(STUDENT_GRADES_WRITE),
                perm(STUDENT_ATTENDANCE_READ), perm(STUDENT_ATTENDANCE_WRITE),
                perm(TEACHER_READ),
                perm(TEACHER_COURSES_MANAGE), perm(TEACHER_GRADES_MANAGE),
                perm(COURSE_READ), perm(COURSE_UPDATE),
                perm(ANNOUNCEMENT_READ),
                perm(PROFILE_READ), perm(PROFILE_WRITE),
                perm(SCHEDULE_READ)
        )));
        roleRepository.save(teacherRole);

        // ELEVE : Lecture seule + profil + emploi du temps (lecture)
        Role studentRole = createRole(ROLE_ELEVE, "Élève");
        studentRole.setPermissions(new HashSet<>(List.of(
                perm(STUDENT_GRADES_READ),
                perm(STUDENT_ATTENDANCE_READ),
                perm(COURSE_READ),
                perm(ANNOUNCEMENT_READ),
                perm(PROFILE_READ), perm(PROFILE_WRITE),
                perm(SCHEDULE_READ)
        )));
        roleRepository.save(studentRole);

        // ========== COMPTES PAR DÉFAUT ==========

        // SuperAdmin
        User superAdmin = new User("superadmin", "superadmin@ecole.fr",
                passwordEncoder.encode("SuperAdmin123!"),
                new HashSet<>(List.of(superAdminRole)));
        userRepository.save(superAdmin);
        log.info("SuperAdmin créé: superadmin@ecole.fr / SuperAdmin123!");

        // Admin
        User admin = new User("admin", "admin@ecole.fr",
                passwordEncoder.encode("Admin123!"),
                new HashSet<>(List.of(adminRole)));
        userRepository.save(admin);
        log.info("Admin créé: admin@ecole.fr / Admin123!");

        // Enseignant test
        User teacher = new User("prof_math", "prof.math@ecole.fr",
                passwordEncoder.encode("Prof123!"),
                new HashSet<>(List.of(teacherRole)));
        userRepository.save(teacher);

        TeacherProfile teacherProfile = TeacherProfile.builder()
                .user(teacher)
                .teacherId("ENS-2026-001")
                .department("Sciences")
                .specialization("Mathématiques")
                .subjects(new HashSet<>(List.of("Mathématiques", "Algèbre")))
                .hireDate(java.time.LocalDate.of(2020, 9, 1))
                .qualification("Doctorat en Mathématiques")
                .phone("+33 6 11 22 33 44")
                .officeLocation("Bâtiment A, Bureau 105")
                .bio("Professeur de mathématiques passionné")
                .tenured(true)
                .build();
        teacherProfileRepository.save(teacherProfile);
        log.info("Enseignant test créé: prof.math@ecole.fr / Prof123!");

        // Élève test
        User student = new User("eleve_test", "eleve.test@ecole.fr",
                passwordEncoder.encode("Eleve123!"),
                new HashSet<>(List.of(studentRole)));
        userRepository.save(student);

        StudentProfile studentProfile = StudentProfile.builder()
                .user(student)
                .studentId("ELEVE-2026-001")
                .registrationNumber("REG-2026-001")
                .gradeLevel("Terminale")
                .className("S2")
                .section("S")
                .academicYear("2025-2026")
                .birthDate(java.time.LocalDate.of(2007, 5, 12))
                .parentName("M. et Mme Dupont")
                .parentPhone("+33 6 55 66 77 88")
                .parentEmail("parents.dupont@email.fr")
                .emergencyContact("+33 6 99 88 77 66")
                .address("25 Avenue des Champs, 75008 Paris")
                .enrollmentDate(java.time.LocalDate.of(2018, 9, 1))
                .scholarship(false)
                .build();
        studentProfileRepository.save(studentProfile);
        log.info("Élève test créé: eleve.test@ecole.fr / Eleve123!");

        log.info("=== INITIALISATION TERMINÉE ===");
    }

    private Permission createPermissionIfNotFound(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name, description)));
    }

    private Role createRole(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> roleRepository.save(new Role(name, description)));
    }

    private Permission perm(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new IllegalStateException("Permission manquante: " + name));
    }
}