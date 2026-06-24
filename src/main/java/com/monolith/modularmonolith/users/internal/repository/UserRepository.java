package com.monolith.modularmonolith.users.internal.repository;

import com.monolith.modularmonolith.users.internal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Rechercher un utilisateur par son adresse email
    Optional<User> findByEmail(String email);

    // Vérifier si un email existe déjà en base de données
    boolean existsByEmail(String email);
}
