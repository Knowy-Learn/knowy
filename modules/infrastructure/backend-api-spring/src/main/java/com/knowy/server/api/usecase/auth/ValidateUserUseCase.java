package com.knowy.server.api.usecase.auth;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.util.PasswordResetInfo;
import com.knowy.security.model.UserSecurityDto;
import com.knowy.server.api.dto.AuthValidatePostRequest;

public class ValidateUserUseCase {

	private final KnowyTokenTools knowyTokenTools;
	private final UserPrivateRepository userPrivateRepository;

	public ValidateUserUseCase(KnowyTokenTools knowyTokenTools, UserPrivateRepository userPrivateRepository) {
		this.knowyTokenTools = knowyTokenTools;
		this.userPrivateRepository = userPrivateRepository;
	}

	public User execute(AuthValidatePostRequest authValidatePostRequest) throws KnowyTokenException,
		KnowyUserNotFoundException {
		UserSecurityDto userSecurityDto = knowyTokenTools.decodeUnverified(
			authValidatePostRequest.getAccessToken(),
			UserSecurityDto.class
		);
		UserPrivate userPrivate = getUserPrivateByIdOrThrow(userSecurityDto.id());
		knowyTokenTools.decode(userPrivate.password().value(), authValidatePostRequest.getAccessToken(), PasswordResetInfo.class);

		return userPrivate.cropToUser();
	}

	private UserPrivate getUserPrivateByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userPrivateRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
	}
}
