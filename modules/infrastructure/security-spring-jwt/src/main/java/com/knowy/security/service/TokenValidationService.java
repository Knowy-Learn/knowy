package com.knowy.security.service;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.util.PasswordResetInfo;
import com.knowy.security.model.UserSecurityDto;

public class TokenValidationService {

	private final KnowyTokenTools knowyTokenTools;
	private final UserPrivateRepository userPrivateRepository;

	public TokenValidationService(KnowyTokenTools knowyTokenTools, UserPrivateRepository userPrivateRepository) {
		this.knowyTokenTools = knowyTokenTools;
		this.userPrivateRepository = userPrivateRepository;
	}

	public User validateUserToken(String token) throws KnowyTokenException, KnowyUserNotFoundException {

		UserSecurityDto userSecurityDto = knowyTokenTools.decodeUnverified(token, UserSecurityDto.class);
		UserPrivate userPrivate = getUserPrivateByIdOrThrow(userSecurityDto.id());
		knowyTokenTools.decode(userPrivate.password().value(), token, PasswordResetInfo.class);

		return userPrivate.cropToUser();
	}

	private UserPrivate getUserPrivateByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userPrivateRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
	}
}
