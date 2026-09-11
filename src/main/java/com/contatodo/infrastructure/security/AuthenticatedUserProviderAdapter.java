package com.contatodo.infrastructure.security;

import com.contatodo.application.port.AuthenticatedUserProvider;
import com.contatodo.domain.entities.User;
import com.contatodo.domain.repositories.UserRepository;
import com.contatodo.shared.constants.AuthConstants;
import com.contatodo.shared.constants.UserConstants;
import com.contatodo.shared.exceptions.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Adapter that resolves the authenticated user from the Spring Security
 * context using the user repository port.
 */
@Component
public class AuthenticatedUserProviderAdapter implements AuthenticatedUserProvider {

    private final UserRepository userRepository;

    /**
     * Creates an authenticated user provider adapter.
     *
     * @param userRepository User repository port.
     */
    public AuthenticatedUserProviderAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentUserOid() {
        User user = findCurrentUser();
        return user.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentUserEmail() {
        return currentAuthentication().getName();
    }

    /**
     * Loads the active user matching the authenticated email.
     *
     * @return Authenticated domain user.
     * @throws UserNotFoundException if there is no authentication or the user does not exist.
     */
    private User findCurrentUser() {
        String email = currentAuthentication().getName();
        return userRepository.findActiveUserByEmail(email, false)
                .orElseThrow(() -> new UserNotFoundException(UserConstants.USER_NOT_FOUND));
    }

    /**
     * Gets the current authentication or fails when absent.
     *
     * @return Current Spring Security authentication.
     * @throws UserNotFoundException if the request is not authenticated.
     */
    private Authentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException(AuthConstants.USER_NOT_AUTHENTICATED);
        }
        return authentication;
    }
}
