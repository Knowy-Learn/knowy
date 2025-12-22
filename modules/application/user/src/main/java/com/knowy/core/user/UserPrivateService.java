package com.knowy.core.user;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.mail.KnowyMailDispatchException;
import com.knowy.core.port.ExternalNotificationDispatcher;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.conflict.KnowyEmailAlreadyTakenException;
import com.knowy.core.user.exception.conflict.KnowyNicknameAlreadyTakenException;
import com.knowy.core.user.exception.conflict.KnowyUnchangedEmailException;
import com.knowy.core.user.exception.resource.KnowyImageNotFoundException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.user.exception.security.KnowyWrongPasswordException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.core.user.exception.validation.KnowyPasswordFormatException;
import com.knowy.core.user.exception.validation.KnowyUserEmailFormatException;
import com.knowy.core.user.port.*;
import com.knowy.core.user.usercase.manage.DeactivateAccountCommand;
import com.knowy.core.user.usercase.manage.DeactivateAccountUseCase;
import com.knowy.core.user.usercase.manage.ReactivateAccountUseCase;
import com.knowy.core.user.usercase.manage.SendRecoveryPasswordUseCase;
import com.knowy.core.user.usercase.register.UserSignUpUseCase;
import com.knowy.core.user.usercase.register.UserSingUpCommand;
import com.knowy.core.user.usercase.update.email.UserUpdateEmailCommand;
import com.knowy.core.user.usercase.update.email.UserUpdateEmailUseCase;
import com.knowy.core.user.usercase.update.password.UserUpdatePasswordCommand;
import com.knowy.core.user.usercase.update.password.UserUpdatePasswordUseCase;
import com.knowy.core.user.util.TokenUserPrivateTool;

/**
 * Service layer for handling user account private operations.
 * <p>
 * Provides a high-level API for registering new users, updating credentials, validating tokens, sending recovery
 * emails, deactivating accounts, and reactivating accounts.
 * <p>
 * Internally delegates execution to dedicated use case classes to enforce business logic.
 */
public class UserPrivateService {

	private final UserSignUpUseCase userSignUpUseCase;
	private final UserUpdatePasswordUseCase userUpdatePasswordUseCase;
	private final UserUpdateEmailUseCase userUpdateEmailUseCase;
	private final TokenUserPrivateTool tokenUserPrivateTool;
	private final SendRecoveryPasswordUseCase sendRecoveryPasswordUseCase;
	private final DeactivateAccountUseCase deactivateAccountUseCase;
	private final ReactivateAccountUseCase reactivateAccountUseCase;

	/**
	 * Constructs a new {@code UserPrivateService} with the provided dependencies.
	 *
	 * @param userRepository                 Repository for public user data access.
	 * @param userPrivateRepository          Repository for private user data access.
	 * @param profileImageRepository         Repository for retrieving profile images.
	 * @param knowyPasswordEncoder           Password encoder for hashing user passwords.
	 * @param knowyTokenTools                Utility for generating and validating security tokens.
	 * @param externalNotificationDispatcher Client for sending system emails.
	 */
	public UserPrivateService(
		UserRepository userRepository,
		UserPrivateRepository userPrivateRepository,
		ProfileImageRepository profileImageRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		KnowyTokenTools knowyTokenTools,
		ExternalNotificationDispatcher externalNotificationDispatcher
	) {
		this(
			userRepository,
			userPrivateRepository,
			profileImageRepository,
			knowyPasswordEncoder,
			knowyTokenTools,
			externalNotificationDispatcher,
			new TokenUserPrivateTool(knowyTokenTools, userPrivateRepository)
		);
	}

	/**
	 * Constructs a new {@code UserPrivateService} with the provided dependencies. This package-private constructor is
	 * primarily intended for dependency injection and testing.
	 *
	 * @param userRepository                 the repository for public user data access.
	 * @param userPrivateRepository          the repository for private user data access.
	 * @param profileImageRepository         the repository for retrieving profile images.
	 * @param knowyPasswordEncoder           the password encoder for hashing user passwords.
	 * @param knowyTokenTools                the utility for generating and validating security tokens.
	 * @param externalNotificationDispatcher the client for sending system notifications and emails.
	 * @param tokenUserPrivateTool           the tool for managing user private tokens.
	 */
	UserPrivateService(
		UserRepository userRepository,
		UserPrivateRepository userPrivateRepository,
		ProfileImageRepository profileImageRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		KnowyTokenTools knowyTokenTools,
		ExternalNotificationDispatcher externalNotificationDispatcher,
		TokenUserPrivateTool tokenUserPrivateTool
	) {
		this.userSignUpUseCase = new UserSignUpUseCase(
			userRepository, userPrivateRepository, knowyPasswordEncoder, profileImageRepository
		);
		this.userUpdatePasswordUseCase = new UserUpdatePasswordUseCase(
			userPrivateRepository, knowyPasswordEncoder, knowyTokenTools
		);
		this.userUpdateEmailUseCase = new UserUpdateEmailUseCase(userPrivateRepository, knowyPasswordEncoder);
		this.tokenUserPrivateTool = tokenUserPrivateTool;
		this.sendRecoveryPasswordUseCase = new SendRecoveryPasswordUseCase(tokenUserPrivateTool, externalNotificationDispatcher);
		this.deactivateAccountUseCase = new DeactivateAccountUseCase(
			tokenUserPrivateTool, externalNotificationDispatcher, knowyPasswordEncoder, userPrivateRepository
		);
		this.reactivateAccountUseCase = new ReactivateAccountUseCase(tokenUserPrivateTool, userPrivateRepository);
	}

	/**
	 * Registers a new user with the given credentials.
	 *
	 * @param command The {@link UserSingUpCommand} containing the user's registration details, including nickname,
	 *                email, password, and gender.
	 * @return The created {@link UserPrivate} entity representing the newly registered user.
	 * @throws KnowyInvalidUserException          if the nickname or email format is invalid.
	 * @throws KnowyImageNotFoundException        if the default profile image cannot be found in the repository.
	 * @throws KnowyPasswordFormatException       if the password does not meet the required security criteria.
	 * @throws KnowyEmailAlreadyTakenException    if the provided email is already registered in the system.
	 * @throws KnowyNicknameAlreadyTakenException if the provided nickname is already in use by another user.
	 * @throws KnowyInvalidUserGenderException    if the gender value provided is not recognized or supported.
	 * @throws KnowyDataAccessException           if an error occurs during the persistence or retrieval of data.
	 */
	public UserPrivate registerNewUser(UserSingUpCommand command)
		throws KnowyDataAccessException, KnowyInvalidUserException, KnowyImageNotFoundException, KnowyPasswordFormatException,
		KnowyEmailAlreadyTakenException, KnowyNicknameAlreadyTakenException, KnowyInvalidUserGenderException {
		return userSignUpUseCase.execute(command);
	}

	/**
	 * Updates the user's password using a recovery token.
	 *
	 * @param command The {@link UserUpdatePasswordCommand} containing the recovery token, the new password, and the
	 *                confirmation of the new password.
	 * @throws KnowyTokenException          If the token is invalid or expired.
	 * @throws KnowyPasswordFormatException If the password format is invalid.
	 * @throws KnowyWrongPasswordException  If the password and confirmation do not match.
	 * @throws KnowyDataAccessException     If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException   If the user associated with the token does not exist.
	 */
	public UserPrivate updatePassword(UserUpdatePasswordCommand command)
		throws KnowyTokenException, KnowyPasswordFormatException, KnowyWrongPasswordException, KnowyDataAccessException {
		return userUpdatePasswordUseCase.execute(command);
	}

	/**
	 * Updates the user's email address.
	 *
	 * @param command The {@link UserUpdateEmailCommand} containing the user's ID, the new email address, and the
	 *                current password for verification.
	 * @throws KnowyUnchangedEmailException  If the new email is the same as the current one.
	 * @throws KnowyWrongPasswordException   If the provided password is incorrect.
	 * @throws KnowyUserEmailFormatException If the email is invalid or already in use.
	 * @throws KnowyDataAccessException      If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException    If no user is found with the given ID.
	 */
	public void updateEmail(UserUpdateEmailCommand command)
		throws KnowyUnchangedEmailException, KnowyWrongPasswordException, KnowyUserEmailFormatException, KnowyDataAccessException {
		userUpdateEmailUseCase.execute(command);
	}

	/**
	 * Checks if the provided token is valid for a user.
	 *
	 * @param token The token to validate.
	 * @return {@code true} if the token is valid; {@code false} otherwise.
	 */
	public boolean isValidUserToken(String token) {
		return tokenUserPrivateTool.isValidToken(token);
	}

	/**
	 * Sends a password recovery email to the given address.
	 *
	 * @param email           The user's email.
	 * @param recoveryBaseUrl The base URL for building the recovery link.
	 * @throws KnowyTokenException        If an error occurs while generating the recovery token.
	 * @throws KnowyMailDispatchException If the email cannot be sent.
	 * @throws KnowyDataAccessException   If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException If no user exists with the provided email.
	 */
	public void sendRecoveryPasswordEmail(Email email, String recoveryBaseUrl)
		throws KnowyTokenException, KnowyMailDispatchException, KnowyDataAccessException {
		sendRecoveryPasswordUseCase.execute(email, recoveryBaseUrl);
	}

	/**
	 * Deactivates a user's account and sends them a recovery email with a reactivation link.
	 *
	 * @param command The {@link DeactivateAccountCommand} containing the user's email, current password, confirmation
	 *                password, and the base URL for the recovery link.
	 * @throws KnowyTokenException         If an error occurs while generating the recovery token.
	 * @throws KnowyMailDispatchException  If the recovery email cannot be sent.
	 * @throws KnowyWrongPasswordException If the provided password is incorrect or does not match the confirmation.
	 * @throws KnowyDataAccessException    If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException  If no user exists with the provided email.
	 */
	public void desactivateUserAccount(DeactivateAccountCommand command)
		throws KnowyTokenException, KnowyMailDispatchException, KnowyWrongPasswordException, KnowyDataAccessException {
		deactivateAccountUseCase.execute(command);
	}

	/**
	 * Reactivates a user's account using the provided token.
	 *
	 * @param token The reactivation token.
	 * @throws KnowyTokenException        If the token is invalid or expired.
	 * @throws KnowyDataAccessException   If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException If no user exists for the given token.
	 */
	public void reactivateUserAccount(String token) throws KnowyTokenException, KnowyDataAccessException {
		reactivateAccountUseCase.execute(token);
	}
}
