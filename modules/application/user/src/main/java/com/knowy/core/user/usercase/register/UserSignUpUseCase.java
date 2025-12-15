package com.knowy.core.user.usercase.register;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Gender;
import com.knowy.core.user.domain.Password;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.conflict.KnowyEmailAlreadyTakenException;
import com.knowy.core.user.exception.conflict.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.resource.KnowyImageNotFoundException;
import com.knowy.core.user.exception.validation.*;
import com.knowy.core.user.port.KnowyPasswordEncoder;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.port.UserRepository;
import com.knowy.core.user.usercase.KnowyUseCase;
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
	 * @throws KnowyInvalidUserException          If the user data is invalid.
	 * @throws KnowyNicknameAlreadyTakenException If the nickname is already in use.
	 * @throws KnowyEmailAlreadyTakenException    If the email is already registered.
	 * @throws KnowyPasswordFormatException       If the password format is invalid.
	 * @throws KnowyInvalidUserGenderException    If the provided gender is invalid.
	 * @throws KnowyImageNotFoundException        If the default profile image cannot be found.
	 */
	public UserPrivate execute(UserSingUpCommand userSingUpCommand)
		throws KnowyImageNotFoundException, KnowyPasswordFormatException, KnowyEmailAlreadyTakenException, KnowyInvalidUserException, KnowyNicknameAlreadyTakenException, KnowyInvalidUserGenderException {

		assertUserNickname(userSingUpCommand.nickname());
		validateEmail(userSingUpCommand.email());
		Password.assertPasswordFormatIsRight(userSingUpCommand.password());

		String encodedPassword = passwordEncoder.encode(userSingUpCommand.password());
		UserPrivate userPrivate = new UserPrivate(
			null,
			userSingUpCommand.nickname(),
			assertParseGender(userSingUpCommand.gender()),
			profileImageRepository.findById(1)
				.orElseThrow(() -> new KnowyImageNotFoundException("Not found profile image")),
			new HashSet<>(),
			new Email(userSingUpCommand.email()),
			new Password(encodedPassword),
			true
		);
		return userPrivateRepository.save(userPrivate);
	}

	private void assertUserNickname(String nickname) throws KnowyInvalidUserException, KnowyNicknameAlreadyTakenException {
		if (StringUtils.isBlank(nickname)) {
			throw new KnowyInvalidUserNicknameException("Invalid nickname");
		}

		if (userRepository.findByNickname(nickname).isPresent()) {
			throw new KnowyNicknameAlreadyTakenException("Nickname already exists");
		}
	}

	private void validateEmail(String email) throws KnowyUserEmailFormatException, KnowyEmailAlreadyTakenException {
		Email.assertValid(email);
		if (userPrivateRepository.findByEmail(email).isPresent()) {
			throw new KnowyEmailAlreadyTakenException("Email already exists");
		}
	}

	private Gender assertParseGender(String gender) throws KnowyInvalidUserGenderException {
		if (gender == null || gender.isBlank()) {
			throw new KnowyInvalidUserGenderException("Gender cannot be null or empty");
		}

		try {
			return Gender.valueOf(gender.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new KnowyInvalidUserGenderException("Invalid gender value" + gender, e);
		}
	}
}
