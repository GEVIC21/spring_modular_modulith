package com.monolith.modularmonolith.identity.api;

public interface UserPasswordVerifier {

    boolean verifyPassword(String email, String rawPassword);
}