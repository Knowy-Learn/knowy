package com.knowy.server.api.usecase.user;

import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyUnauthorizedException;
import com.knowy.server.api.dto.UserNavbarGet200Response;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Use case responsible for retrieving basic user information required for the application's navigation bar.
 * <p>
 * This class fetches the current session's identity to display minimal profile details such as the nickname and the
 * profile picture URL.
 */
public class GetNavbarUserDataUseCase {

	/**
	 * Executes the logic to retrieve the authenticated user's display information.
	 *
	 * @return a {@link UserNavbarGet200Response} containing the user's nickname and avatar URL.
	 * @throws KnowyUnauthorizedException if no valid authenticated user is found in the security context.
	 */
	public UserNavbarGet200Response execute() {
		User user = getAuthenticatedUser();

		return new UserNavbarGet200Response()
			.username(user.nickname())
			.avatarUrl(user.profileImage().url());
	}

	private User getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();

		if (!(principal instanceof User user)) {
			String principalClass = principal != null ? principal.getClass().getName() : "null";
			throw new KnowyUnauthorizedException("Expected principal of type User, but got: " + principalClass);
		}

		return user;
	}
}
