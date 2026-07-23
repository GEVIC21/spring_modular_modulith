package com.monolith.modularmonolith.identity.internal.domain.repository;

import com.monolith.modularmonolith.identity.internal.domain.model.ProfileType;
import com.monolith.modularmonolith.identity.internal.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Port JPA pour l'entité User.
 * Fournit des requêtes optimisées avec FETCH JOIN pour éviter le problème N+1.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // === Recherche basique ===

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    // === Recherche avec profils (FETCH JOIN) ===

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.studentProfile " +
            "LEFT JOIN FETCH u.teacherProfile " +
            "LEFT JOIN FETCH u.adminProfile " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithProfiles(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.studentProfile " +
            "LEFT JOIN FETCH u.teacherProfile " +
            "LEFT JOIN FETCH u.adminProfile " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithProfiles(@Param("id") Long id);

    // === Liste paginée avec filtres ===

    @Query("SELECT u FROM User u " +
            "WHERE (:profileType IS NULL OR u.profileType = :profileType) " +
            "AND (:active IS NULL OR u.active = :active) " +
            "AND (:search IS NULL OR " +
            "     LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> findAllWithFilters(
            @Param("profileType") ProfileType profileType,
            @Param("active") Boolean active,
            @Param("search") String search,
            Pageable pageable);

    // === Recherche par IDs ===

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.studentProfile " +
            "LEFT JOIN FETCH u.teacherProfile " +
            "LEFT JOIN FETCH u.adminProfile " +
            "WHERE u.id IN :ids")
    List<User> findAllByIdWithProfiles(@Param("ids") List<Long> ids);

    // === Statistiques ===

    @Query("SELECT COUNT(u) FROM User u WHERE u.profileType = :profileType")
    long countByProfileType(@Param("profileType") ProfileType profileType);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = :active")
    long countByActive(@Param("active") boolean active);

    @Query("SELECT COUNT(u) FROM User u WHERE u.profileType = :profileType AND u.active = :active")
    long countByProfileTypeAndActive(@Param("profileType") ProfileType profileType, @Param("active") boolean active);
}