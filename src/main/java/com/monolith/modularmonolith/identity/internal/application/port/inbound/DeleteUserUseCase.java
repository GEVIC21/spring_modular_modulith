package com.monolith.modularmonolith.identity.internal.application.port.inbound;

/**
 * Port inbound : Suppression permanente d'un utilisateur (hard delete).
 * Réservé au SUPER_ADMIN.
 */
public interface DeleteUserUseCase {

    void execute(Long userId);
}