package com.monolith.modularmonolith.users.internal.repository;

import com.monolith.modularmonolith.users.internal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username); // <-- Cette ligne résout l'erreur 'Cannot resolve method'
}
