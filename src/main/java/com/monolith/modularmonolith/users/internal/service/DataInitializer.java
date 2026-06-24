package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.internal.model.Permission;
import com.monolith.modularmonolith.users.internal.model.Role;
import com.monolith.modularmonolith.users.internal.model.User;
import com.monolith.modularmonolith.users.internal.repository.PermissionRepository;
import com.monolith.modularmonolith.users.internal.repository.RoleRepository;
import com.monolith.modularmonolith.users.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Sécurité : On initialise uniquement si la base de données de sécurité est totalement vide
        if (roleRepository.count() == 0) {
            log.info("--- INITIALISATION DE LA BASE DE DONNÉES AGNOSTIQUE (ENTREPRISE LEVEL) ---");

            // 1. Création des Permissions Universelles (Fonctionne pour École, Assurance, etc.)
            Permission readPerm = createPermissionIfNotFound("document:read", "Autorise la lecture des ressources");
            Permission writePerm = createPermissionIfNotFound("document:write", "Autorise la création/modification");
            Permission deletePerm = createPermissionIfNotFound("document:delete", "Autorise la suppression critique");

            // 2. Création et configuration du Rôle Utilisateur Standard (ROLE_USER)
            Role userRole = new Role("ROLE_USER", "Rôle utilisateur standard pour les clients ou élèves");
            userRole.setPermissions(Set.of(readPerm));
            roleRepository.save(userRole);
            log.info("Rôle '{}' créé avec {} permission(s).", userRole.getName(), userRole.getPermissions().size());

            // 3. Création et configuration du Rôle Administrateur (ROLE_ADMIN)
            Role adminRole = new Role("ROLE_ADMIN", "Super-administrateur de la plateforme (Directeur, Superviseur)");
            adminRole.setPermissions(Set.of(readPerm, writePerm, deletePerm));
            roleRepository.save(adminRole);
            log.info("Rôle '{}' créé avec {} permission(s) globales.", adminRole.getName(), adminRole.getPermissions().size());

            // 4. Création d'un compte Administrateur par défaut pour vos tests de sécurité
            User defaultAdmin = new User();
            defaultAdmin.setUsername("superadmin");
            defaultAdmin.setEmail("admin@monolith.com");
            defaultAdmin.setPassword(passwordEncoder.encode("admin1234")); // Hash du mot de passe
            defaultAdmin.setRoles(new HashSet<>(Set.of(adminRole))); // Attribution du rôle

            userRepository.save(defaultAdmin);
            log.info("Compte administrateur par défaut créé ! [Email: admin@monolith.com | MDP: admin1234]");
            log.info("--- FIN DE L'INITIALISATION RÉUSSIE ---");
        }
    }

    /**
     * Méthode utilitaire pour éviter les doublons de permissions
     */
    private Permission createPermissionIfNotFound(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission(name, description);
                    log.info("Création de la permission technique: {}", name);
                    return permissionRepository.save(permission);
                });
    }
}
