package com.monolith.modularmonolith.users.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserLookup {
    Optional<UserSummary> findById(Long id);
    List<UserSummary> findAllById(Set<Long> ids);
    Optional<UserSummary> findByEmail(String email);
    boolean existsById(Long id);
}