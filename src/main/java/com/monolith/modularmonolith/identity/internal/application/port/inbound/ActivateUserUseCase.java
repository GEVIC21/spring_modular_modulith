package com.monolith.modularmonolith.identity.internal.application.port.inbound;

/**
 * Port inbound : Réactivation d'un compte utilisateur.
 */
public interface ActivateUserUseCase {

    void execute(Long userId);
}