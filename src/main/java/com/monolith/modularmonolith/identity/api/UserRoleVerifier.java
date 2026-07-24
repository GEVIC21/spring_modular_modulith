package com.monolith.modularmonolith.identity.api;

public interface UserRoleVerifier {

    boolean hasRole(Long userId, String role);

    boolean hasAnyRole(Long userId, String... roles);
}