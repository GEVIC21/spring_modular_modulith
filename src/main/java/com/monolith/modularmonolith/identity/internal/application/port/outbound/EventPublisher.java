package com.monolith.modularmonolith.identity.internal.application.port.outbound;

/**
 * Port sortant pour la publication d'événements métier.
 * Découple le module identity de l'implémentation du bus d'événements.
 */
public interface EventPublisher {

    /**
     * Publie un événement métier.
     *
     * @param event L'événement à publier (ex: UserCreatedEvent, PasswordResetEvent)
     */
    void publish(Object event);
}