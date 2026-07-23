package com.monolith.modularmonolith.identity.internal.application.port.inbound;

import org.springframework.web.multipart.MultipartFile;

/**
 * Port inbound : Mise à jour de l'avatar d'un utilisateur.
 */
public interface UpdateAvatarUseCase {

    void execute(String email, MultipartFile file);
}