package com.knowy.server.api.usecase.user;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.server.api.controller.exception.KnowyUnauthorizedException;
import com.knowy.server.api.dto.UserNavbarGet200Response;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class GetNavbarUserDataUseCase {

	// TODO:
	public UserNavbarGet200Response execute() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Object principal = auth.getPrincipal();

		if (!(principal instanceof User user)) {
			String principalClass = principal != null ? principal.getClass().getName() : "null";
			throw new KnowyUnauthorizedException("Expected principal of type User, but got: " + principalClass);
		}

		return new UserNavbarGet200Response()
			.username(user.nickname())
			.avatarUrl(user.profileImage().url());
	}
}
