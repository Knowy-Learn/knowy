package com.knowy.server.application.usecase.update.password;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.exception.validation.user.KnowyPasswordFormatException;
import com.knowy.server.application.exception.validation.user.KnowyWrongPasswordException;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyPasswordEncoder;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.application.usecase.KnowyUseCase;
import com.knowy.server.domain.Password;
import com.knowy.server.domain.UserPrivate;

import java.util.Objects;

/**
 * Use case responsible for updating a user's password.
 */
public class UserUpdatePasswordUseCase implements KnowyUseCase<UserUpdatePasswordCommand, UserPrivate> {

	private final UserPrivateRepository userPrivateRepository;
	private final KnowyPasswordEncoder knowyPasswordEncoder;
	private final KnowyTokenTools tokenTools;

	/**
	 * Constructs a new {@code UserUpdatePasswordUseCase} with the required dependencies.
	 *
	 * @param userPrivateRepository Repository for accessing and persisting private user data.
	 * @param knowyPasswordEncoder  Adapter for encoding passwords.
	 * @param tokenTools            Utility for decoding and verifying password reset tokens.
	 */
	public UserUpdatePasswordUseCase(
		UserPrivateRepository userPrivateRepository,
		KnowyPasswordEncoder knowyPasswordEncoder,
		KnowyTokenTools tokenTools
	) {
		this.userPrivateRepository = userPrivateRepository;
		this.knowyPasswordEncoder = knowyPasswordEncoder;
		this.tokenTools = tokenTools;
	}

	/**
	 * Executes the password update process.
	 * <p>
	 * Validates that the new password matches the confirmation password and meets format requirements. Verifies the
	 * password reset token and updates the user's password with the encoded value in the database.
	 *
	 * @param command Command containing the password reset token, new password, and confirmation password.
	 * @return The updated {@link UserPrivate} entity.
	 * @throws KnowyPasswordFormatException If the new password format is invalid.
	 * @throws KnowyTokenException          If the password reset token is invalid or expired.
	 * @throws KnowyUserNotFoundException   If the user referenced by the token does not exist.
	 * @throws KnowyWrongPasswordException  If the new password and confirmation password do not match.
	 */
	@Override
	public UserPrivate execute(UserUpdatePasswordCommand command)
		throws KnowyPasswordFormatException, KnowyTokenException, KnowyUserNotFoundException, KnowyWrongPasswordException {

		Objects.requireNonNull(command.password(), "A password should be specified");
		Password.assertPasswordFormatIsRight(command.password());
		validateRawPasswordsMatch(command.password(), command.confirmPassword());

		UserPrivate userPrivate = verifyPasswordToken(command.token());
		String encodedPassword = knowyPasswordEncoder.encode(command.password());
		UserPrivate newUserPrivate = new UserPrivate(
			userPrivate.cropToUser(),
			userPrivate.email(),
			new Password(encodedPassword)
		);
		return userPrivateRepository.save(newUserPrivate);
	}

	private void validateRawPasswordsMatch(String password, String confirmPassword) throws KnowyWrongPasswordException {
		if (!password.equals(confirmPassword)) {
			throw new KnowyWrongPasswordException("Passwords do not match");
		}
	}

	private UserPrivate verifyPasswordToken(String token) throws KnowyTokenException, KnowyUserNotFoundException {
		PasswordResetInfo passwordResetInfo = tokenTools.decodeUnverified(token, PasswordResetInfo.class);
		UserPrivate userPrivate = getUserByIdOrThrow(passwordResetInfo.userId());
		tokenTools.decode(userPrivate.password().value(), token, PasswordResetInfo.class);
		return userPrivate;
	}

	private UserPrivate getUserByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userPrivateRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found with id: " + userId));
	}
}
