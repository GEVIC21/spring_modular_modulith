package com.monolith.modularmonolith.users.api;

import java.util.Set;

public interface UserRoleVerifier {
    boolean hasRole(Long userId, String roleName);
    boolean hasAnyRole(Long userId, Set<String> roleNames);
}