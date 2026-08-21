package com.contatodo.application.port;

import com.contatodo.domain.entities.User;

/**
 * Outbound port for resolving the currently authenticated user.
 *
 * <p>Replaces static access to the security context so use cases receive
 * an injectable collaborator instead of calling a service locator.</p>
 */
public interface AuthenticatedUserProvider {

    /**
     * Gets the identifier of the currently authenticated user.
     *
     * @return Authenticated user identifier.
     * @throws UserNotFoundException if there is no authenticated user or it cannot be resolved.
     */
    String getCurrentUserOid();

    /**
     * Gets the email of the currently authenticated user.
     *
     * @return Authenticated user email.
     * @throws UserNotFoundException if there is no authenticated user.
     */
    String getCurrentUserEmail();
}
