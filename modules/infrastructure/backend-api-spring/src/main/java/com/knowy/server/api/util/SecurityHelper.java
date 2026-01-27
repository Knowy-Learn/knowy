package com.knowy.server.api.util;

import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyUnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility component to provide centralized access to the security context. It abstracts the logic for retrieving and
 * validating the currently authenticated principal.
 */
public class SecurityHelper {

	/**
	 * Retrieves the currently authenticated user from the SecurityContext. * @return The authenticated {@link User}
	 * principal.
	 *
	 * @throws KnowyUnauthorizedException if the authentication is missing, unauthenticated, or if the principal is not
	 *                                    an instance of {@link User}.
	 */
	public User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null || !auth.isAuthenticated()) {
			throw new KnowyUnauthorizedException("No active authentication found in security context.");
		}

		Object principal = auth.getPrincipal();

		if (!(principal instanceof User user)) {
			String principalClass = (principal != null) ? principal.getClass().getName() : "null";
			throw new KnowyUnauthorizedException(
				String.format("Expected principal of type User, but encountered: %s", principalClass)
			);
		}
		return user;
	}
}
