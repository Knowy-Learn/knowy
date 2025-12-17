package com.knowy.security.service;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.domain.User;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.security.usecase.ValidateUserUseCase;

public class TokenValidationService {

	private final ValidateUserUseCase validateUserUseCase;

	public TokenValidationService(KnowyTokenTools knowyTokenTools, UserPrivateRepository userPrivateRepository) {
		this.validateUserUseCase = new ValidateUserUseCase(knowyTokenTools, userPrivateRepository);
	}

	public User validateUserToken(String token) throws KnowyTokenException, KnowyDataAccessException {
		return validateUserUseCase.execute(token);
	}
}
