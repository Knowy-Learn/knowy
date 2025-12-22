package com.knowy.core.user.usercase.register;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.validation.KnowyInvalidDataException;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.conflict.KnowyEmailAlreadyTakenException;
import com.knowy.core.user.exception.conflict.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.resource.KnowyImageNotFoundException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserNicknameException;
import com.knowy.core.user.exception.validation.KnowyPasswordFormatException;
import com.knowy.core.user.exception.validation.KnowyUserEmailFormatException;
import com.knowy.core.user.port.KnowyPasswordEncoder;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.port.UserRepository;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.core.util.StringUtils;

import java.util.HashSet;

/**
 * Use case class responsible for handling the user sign-up process.
 */
public class UserSignUpUseCase implements KnowyUseCase<UserSingUpCommand, UserPrivate> {

	private final UserRepository userRepository;
	private final UserPrivateRepository userPrivateRepository;
	private final ProfileImageRepository profileImageRepository;
	private final KnowyPasswordEncoder passwordEncoder;

	/**
	 * Constructs a new UserSignUpUseCase with the required dependencies.
	 *
	 * @param userRepository         Repository for user public data access.
	 * @param userPrivateRepository  Repository for user private data access.
	 * @param passwordEncoder        Password encoder for encrypting user passwords.
	 * @param profileImageRepository Repository for profile image access.
	 */
	public UserSignUpUseCase(
		UserRepository userRepository,
		UserPrivateRepository userPrivateRepository,
		KnowyPasswordEncoder passwordEncoder,
		ProfileImageRepository profileImageRepository
	) {
		this.userRepository = userRepository;
		this.userPrivateRepository = userPrivateRepository;
		this.passwordEncoder = passwordEncoder;
		this.profileImageRepository = profileImageRepository;
	}

	/**
	 * Executes the user sign-up process with the given sign-up command. Validates user data, encodes the password, and
	 * creates a new user record in the persistence.
	 *
	 * @param userSingUpCommand Command containing the user registration data such as nickname, email, password, and
	 *                          gender.
	 * @return The newly created and persisted {@link UserPrivate} entity.
	 * @throws KnowyNicknameAlreadyTakenException If the nickname is already in use.
	 * @throws KnowyEmailAlreadyTakenException    If the email is already registered.
	 * @throws KnowyPasswordFormatException       If the password format is invalid.
	 * @throws KnowyInvalidDataException          If the user data is invalid.
	 * @throws KnowyInvalidUserGenderException    If the provided gender is invalid.
	 * @throws KnowyDataAccessException           If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyImageNotFoundException        If the default profile image cannot be found.
	 */
	public UserPrivate execute(UserSingUpCommand userSingUpCommand)
		throws KnowyDataAccessException, KnowyPasswordFormatException, KnowyEmailAlreadyTakenException,
		KnowyInvalidDataException, KnowyNicknameAlreadyTakenException {

		assertUserNickname(userSingUpCommand.nickname());
		validateEmail(userSingUpCommand.email());
		Password.assertPasswordFormatIsRight(userSingUpCommand.password().value());

		String encodedPassword = passwordEncoder.encode(userSingUpCommand.password().value());
		UserPrivate userPrivate = new UserPrivate(
			null,
			userSingUpCommand.nickname(),
			userSingUpCommand.gender(),
			profileImageRepository.findById(1)
				.orElseThrow(() -> new KnowyImageNotFoundException("Not found profile image")),
			new HashSet<>(),
			userSingUpCommand.email(),
			new Password(encodedPassword),
			true
		);
		return userPrivateRepository.save(userPrivate);
	}

	private void assertUserNickname(String nickname) throws KnowyInvalidDataException, KnowyNicknameAlreadyTakenException, KnowyDataAccessException {
		if (StringUtils.isBlank(nickname)) {
			throw new KnowyInvalidUserNicknameException("Invalid nickname");
		}

		if (userRepository.findByNickname(nickname).isPresent()) {
			throw new KnowyNicknameAlreadyTakenException("Nickname already exists");
		}
	}

	private void validateEmail(Email email) throws KnowyUserEmailFormatException, KnowyEmailAlreadyTakenException, KnowyDataAccessException {
		Email.assertValid(email.value());
		if (userPrivateRepository.findByEmail(email.value()).isPresent()) {
			throw new KnowyEmailAlreadyTakenException("Email already exists");
		}
	}
}
