package com.knowy.server.application.usecase.register;

import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyImageNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserNicknameException;
import com.knowy.server.application.exception.validation.user.KnowyInvalidUserPasswordFormatException;
import com.knowy.server.application.ports.*;
import com.knowy.server.application.util.StringUtils;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.UserPrivate;

import java.util.HashSet;

public class UserSignUpUseCase {

	private final UserRepository userRepository;
	private final UserPrivateRepository userPrivateRepository;
	private final ProfileImageRepository profileImageRepository;
	private final KnowyPasswordEncoder passwordEncoder;
	private final KnowyPasswordChecker passwordChecker;

	public UserSignUpUseCase(
		UserRepository userRepository,
		UserPrivateRepository userPrivateRepository,
		KnowyPasswordEncoder passwordEncoder,
		KnowyPasswordChecker passwordChecker,
		ProfileImageRepository profileImageRepository
	) {
		this.userRepository = userRepository;
		this.userPrivateRepository = userPrivateRepository;
		this.passwordEncoder = passwordEncoder;
		this.passwordChecker = passwordChecker;
		this.profileImageRepository = profileImageRepository;
	}

	public UserPrivate execute(UserSingUpCommand userSingUpCommand)
		throws KnowyInvalidUserException, KnowyImageNotFoundException {

		assertUserNickname(userSingUpCommand.nickname());

		Email.assertValid(userSingUpCommand.email());
		if (userPrivateRepository.findByEmail(userSingUpCommand.email()).isPresent()) {
			throw new KnowyInvalidUserEmailException("Email already exists");
		}

		if (!passwordChecker.isRightPasswordFormat(userSingUpCommand.password())) {
			throw new KnowyInvalidUserPasswordFormatException("Invalid password format");
		}

		UserPrivate userPrivate = new UserPrivate(
			null,
			userSingUpCommand.nickname(),
			profileImageRepository.findById(1)
				.orElseThrow(() -> new KnowyImageNotFoundException("Not found profile image")),
			new HashSet<>(),
			new Email(userSingUpCommand.email()),
			passwordEncoder.encode(userSingUpCommand.password()),
			true
		);
		return userPrivateRepository.save(userPrivate);
	}

	private void assertUserNickname(String nickname) throws KnowyInvalidUserException {
		if (StringUtils.isBlank(nickname)) {
			throw new KnowyInvalidUserNicknameException("Invalid nickname");
		}

		if (userRepository.findByNickname(nickname).isPresent()) {
			throw new KnowyInvalidUserNicknameException("Nickname already exists");
		}
	}

}
