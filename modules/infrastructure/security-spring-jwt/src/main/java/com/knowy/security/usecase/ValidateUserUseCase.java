package com.knowy.security.usecase;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.usercase.KnowyUseCase;
import com.knowy.core.user.util.PasswordResetInfo;
import com.knowy.security.model.UserSecurityDto;

public class ValidateUserUseCase implements KnowyUseCase<String, User> {

	private final KnowyTokenTools knowyTokenTools;
	private final UserPrivateRepository userPrivateRepository;

	public ValidateUserUseCase(KnowyTokenTools knowyTokenTools, UserPrivateRepository userPrivateRepository) {
		this.knowyTokenTools = knowyTokenTools;
		this.userPrivateRepository = userPrivateRepository;
	}

	public User execute(String token) throws KnowyTokenException, KnowyUserNotFoundException {
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
