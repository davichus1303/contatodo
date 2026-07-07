package com.contatodo.shared.utils;

import com.contatodo.application.services.UserService;
import com.contatodo.domain.entities.User;
import com.contatodo.shared.exceptions.UserNotFoundException;
import com.contatodo.shared.constants.AuthConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for security-related operations.
 * Provides methods to extract user information from the security context.
 */
public class SecurityUtils {

    /**
     * Gets the currently authenticated user.
     *
     * @param userService User service to retrieve user entity.
     * @return Authenticated user.
     * @throws UserNotFoundException if user is not authenticated or not found.
     */
    public static User getCurrentUser(UserService userService) {
        String email = getCurrentUserEmail();
        return userService.getUserEntityByEmail(email);
    }

    /**
     * Gets the email of the currently authenticated user.
     *
     * @return User email.
     * @throws UserNotFoundException if user is not authenticated.
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException(AuthConstants.USER_NOT_AUTHENTICATED);
        }
        return authentication.getName();
    }

    /**
     * Gets the user OID (ID) of the currently authenticated user.
     *
     * @param userService User service to retrieve user entity.
     * @return User OID.
     * @throws UserNotFoundException if user is not authenticated or not found.
     */
    public static String getCurrentUserOid(UserService userService) {
        User user = getCurrentUser(userService);
        return user.getId();
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return True if user is authenticated.
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}