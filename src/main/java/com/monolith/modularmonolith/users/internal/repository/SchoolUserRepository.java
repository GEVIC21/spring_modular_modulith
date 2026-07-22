package com.monolith.modularmonolith.users.internal.repository;

import com.monolith.modularmonolith.users.internal.model.SchoolUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolUserRepository extends JpaRepository<SchoolUser, Long> {

    Optional<SchoolUser> findByEmail(String email);

    Optional<SchoolUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM SchoolUser u WHERE " +
            "(:profileType IS NULL OR u.profileType = :profileType) AND " +
            "(:search IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:active IS NULL OR u.active = :active)")
    Page<SchoolUser> findAllWithFilters(
            @Param("profileType") String profileType,
            @Param("search") String search,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query("SELECT u FROM SchoolUser u LEFT JOIN FETCH u.studentProfile " +
            "LEFT JOIN FETCH u.teacherProfile LEFT JOIN FETCH u.adminProfile " +
            "WHERE u.id = :id")
    Optional<SchoolUser> findByIdWithProfiles(@Param("id") Long id);

    @Query("SELECT u FROM SchoolUser u LEFT JOIN FETCH u.studentProfile " +
            "LEFT JOIN FETCH u.teacherProfile LEFT JOIN FETCH u.adminProfile " +
            "WHERE u.email = :email")
    Optional<SchoolUser> findByEmailWithProfiles(@Param("email") String email);

    long countByProfileType(String profileType);

    long countByActive(boolean active);
}