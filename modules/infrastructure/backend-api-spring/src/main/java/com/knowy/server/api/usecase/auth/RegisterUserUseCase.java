package com.knowy.server.api.usecase.auth;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.UserPrivateService;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Gender;
import com.knowy.core.user.domain.Password;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.conflict.KnowyEmailAlreadyTakenException;
import com.knowy.core.user.exception.conflict.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.exception.validation.KnowyInvalidDataException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.core.user.exception.validation.KnowyPasswordFormatException;
import com.knowy.core.user.port.*;
import com.knowy.core.user.usercase.KnowyUseCase;
import com.knowy.core.user.usercase.register.UserSingUpCommand;
import com.knowy.security.model.UserSecurityDto;
import com.knowy.server.api.dto.AuthRegisterPost201Response;
import com.knowy.server.api.dto.AuthRegisterPostRequest;

public class RegisterUserUseCase implements KnowyUseCase<AuthRegisterPostRequest, AuthRegisterPost201Response> {

	private final UserPrivateService userPrivateService;
	private final KnowyTokenTools knowyTokenTools;

	public RegisterUserUseCase(
		UserRepository userRepository,
		UserPrivateRepository userPrivateRepository,
		ProfileImageRepository profileImageRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		KnowyTokenTools knowyTokenTools
	) {
		this.knowyTokenTools = knowyTokenTools;
		this.userPrivateService = new UserPrivateService(
			userRepository,
			userPrivateRepository,
			profileImageRepository,
			knowyPasswordEncoder,
			knowyTokenTools,
			externalNotification -> {
			} // TODO: Implement external notification
		);
	}

	@Override
	public AuthRegisterPost201Response execute(AuthRegisterPostRequest request)
		throws KnowyInvalidDataException, KnowyPasswordFormatException, KnowyDataAccessException, KnowyTokenException, KnowyEmailAlreadyTakenException, KnowyNicknameAlreadyTakenException, KnowyInvalidUserGenderException {

		UserPrivate user = register(request);
		String token = generateToken(user);

		return new AuthRegisterPost201Response().accessToken(token);
	}

	private UserPrivate register(AuthRegisterPostRequest request)
		throws KnowyDataAccessException, KnowyInvalidDataException, KnowyPasswordFormatException,
		KnowyEmailAlreadyTakenException, KnowyNicknameAlreadyTakenException, KnowyInvalidUserGenderException {

		return userPrivateService.registerNewUser(
			new UserSingUpCommand(
				request.getNickname(),
				Gender.fromValue(request.getGender().getValue()),
				new Email(request.getEmail()),
				new Password(request.getPassword())
			)
		);
	}

	private String generateToken(UserPrivate userPrivate) throws KnowyTokenException {
		return knowyTokenTools.encode(
			new UserSecurityDto(userPrivate.cropToUser()),
			userPrivate.password().value()
		);
	}
}
