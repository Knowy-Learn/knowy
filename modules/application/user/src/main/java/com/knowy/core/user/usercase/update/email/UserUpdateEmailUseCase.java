package com.knowy.core.user.usercase.update.email;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.conflict.KnowyUnchangedEmailException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.exception.security.KnowyWrongPasswordException;
import com.knowy.core.user.exception.validation.KnowyUserEmailFormatException;
import com.knowy.core.user.port.KnowyPasswordEncoder;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.util.KnowyUseCase;

import java.util.Objects;

/**
 * Use case responsible for updating a user's email address.
 */
public class UserUpdateEmailUseCase implements KnowyUseCase<UserUpdateEmailCommand, UserPrivate> {

	private final UserPrivateRepository userPrivateRepository;
	private final KnowyPasswordEncoder knowyPasswordEncoder;

	/**
	 * Constructs a new instance of {@code UserUpdateEmailUseCase}.
	 *
	 * @param userPrivateRepository Repository for accessing and persisting private user data.
	 * @param knowyPasswordEncoder  Adapter for verifying user passwords.
	 */
	public UserUpdateEmailUseCase(UserPrivateRepository userPrivateRepository, KnowyPasswordEncoder knowyPasswordEncoder) {
		this.userPrivateRepository = userPrivateRepository;
		this.knowyPasswordEncoder = knowyPasswordEncoder;
	}


	/**
	 * Executes the process of updating a user's email.
	 * <p>
	 * The operation verifies that the new email is different from the current one, is not already taken by another
	 * account, and that the provided password is correct. If all validations pass, the user's email is updated and
	 * persisted in the database.
	 *
	 * @param command Command containing the user ID, new email, and password for verification.
	 * @return The updated {@link UserPrivate} entity.
	 * @throws KnowyUnchangedEmailException  If the new email is the same as the current one.
	 * @throws KnowyUserEmailFormatException If the new email is already in use by another user.
	 * @throws KnowyWrongPasswordException   If the provided password does not match the user's current password.
	 * @throws KnowyDataAccessException      If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException    If no user is found with the provided ID.
	 */
	@Override
	public UserPrivate execute(UserUpdateEmailCommand command)
		throws KnowyUnchangedEmailException, KnowyUserEmailFormatException, KnowyWrongPasswordException, KnowyDataAccessException {

		UserPrivate userPrivate = getByIdOrThrow(command.userId());

		validateEmailIsDifferent(command.email(), userPrivate.email().value());
		validateEmailIsNotTaken(command.email());
		knowyPasswordEncoder.assertHasPassword(userPrivate, command.password());

		UserPrivate newUserPrivate = buildUpdateUser(userPrivate, command.email());
		return userPrivateRepository.save(newUserPrivate);
	}

	private UserPrivate getByIdOrThrow(int userId) throws KnowyDataAccessException {
		return userPrivateRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found with ID: " + userId));
	}

	private void validateEmailIsDifferent(String newEmail, String currentEmail) throws KnowyUnchangedEmailException {
		if (Objects.equals(newEmail, currentEmail)) {
			throw new KnowyUnchangedEmailException(
				"Email must be different from the current one."
			);
		}
	}

	private void validateEmailIsNotTaken(String email) throws KnowyUserEmailFormatException, KnowyDataAccessException {
		if (userPrivateRepository.findByEmail(email).isPresent()) {
			throw new KnowyUserEmailFormatException(
				"The provided email is already associated with an existing account."
			);
		}
	}

	private UserPrivate buildUpdateUser(UserPrivate user, String email) {
		return new UserPrivate(
			user.cropToUser(),
			new Email(email),
			user.password()
		);
	}
}
