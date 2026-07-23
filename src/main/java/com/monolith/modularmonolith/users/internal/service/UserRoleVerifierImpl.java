package com.monolith.modularmonolith.users.internal.service;

import com.monolith.modularmonolith.users.api.UserRoleVerifier;
import com.monolith.modularmonolith.users.internal.repository.SchoolUserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserRoleVerifierImpl implements UserRoleVerifier {

    private final SchoolUserRepository schoolUserRepository;

    public UserRoleVerifierImpl(SchoolUserRepository schoolUserRepository) {
        this.schoolUserRepository = schoolUserRepository;
    }

    @Override
    public boolean hasRole(Long userId, String roleName) {
        return schoolUserRepository.findById(userId)
                .map(user -> user.hasRole(roleName))
                .orElse(false);
    }

    @Override
    public boolean hasAnyRole(Long userId, Set<String> roleNames) {
        return schoolUserRepository.findById(userId)
                .map(user -> user.hasAnyRole(roleNames))
                .orElse(false);
    }
}