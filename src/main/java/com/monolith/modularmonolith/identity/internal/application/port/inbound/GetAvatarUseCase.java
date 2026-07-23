package com.monolith.modularmonolith.identity.internal.application.port.inbound;

/**
 * Port inbound : Récupération et suppression de l'avatar.
 */
public interface GetAvatarUseCase {

    String getFilename(String email);

    void delete(String email);
}