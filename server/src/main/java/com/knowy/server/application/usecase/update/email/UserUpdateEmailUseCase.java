package com.knowy.server.application.usecase.update.email;

import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.domain.exception.KnowyUserEmailFormatException;
import com.knowy.server.application.exception.validation.user.KnowyUnchangedEmailException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.usecase.KnowyUseCase;
import com.knowy.server.domain.Email;
import com.knowy.server.domain.UserPrivate;

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
	 * @param userPrivateRepository   Repository for accessing and persisting private user data.
	 * @param knowyPasswordEncoder  Adapter for verifying user passwords.
	 */
	public UserUpdateEmailUseCase(UserPrivateRepository userPrivateRepository, KnowyPasswordEncoder knowyPasswordEncoder) {
		this.userPrivateRepository = userPrivateRepository;
		this.knowyPasswordEncoder = knowyPasswordEncoder;
	}


	/**
	 * Executes the process of updating a user's email.
	 * <p>
	 * The operation verifies that the new email is different from the current one,
	 * is not already taken by another account, and that the provided password is correct.
	 * If all validations pass, the user's email is updated and persisted in the database.
	 *
	 * @param command Command containing the user ID, new email, and password for verification.
	 * @return The updated {@link UserPrivate} entity.
	 * @throws KnowyUnchangedEmailException    If the new email is the same as the current one.
	 * @throws KnowyUserEmailFormatException  If the new email is already in use by another user.
	 * @throws KnowyWrongPasswordException     If the provided password does not match the user's current password.
	 * @throws KnowyUserNotFoundException      If no user is found with the provided ID.
	 */
	@Override
	public UserPrivate execute(UserUpdateEmailCommand command)
		throws KnowyUnchangedEmailException, KnowyUserEmailFormatException, KnowyWrongPasswordException, KnowyUserNotFoundException {

		UserPrivate userPrivate = getByIdOrThrow(command.userId());

		validateEmailIsDifferent(command.email(), userPrivate.email().value());
		validateEmailIsNotTaken(command.email());
		knowyPasswordEncoder.assertHasPassword(userPrivate, command.password());

		UserPrivate newUserPrivate = buildUpdateUser(userPrivate, command.email());
		return userPrivateRepository.save(newUserPrivate);
	}

	private UserPrivate getByIdOrThrow(int userId) throws KnowyUserNotFoundException {
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

	private void validateEmailIsNotTaken(String email) throws KnowyUserEmailFormatException {
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
