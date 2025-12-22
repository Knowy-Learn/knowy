package com.knowy.core.user.util;

import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.core.user.exception.security.KnowyTokenException;
import com.knowy.core.user.port.KnowyTokenTools;
import com.knowy.core.user.port.UserPrivateRepository;

import java.util.Objects;

/**
 * Utility class for handling and verifying user-related tokens. <p>
 */
public class TokenUserPrivateTool {

	private final KnowyTokenTools tokenTools;
	private final UserPrivateRepository userPrivateRepository;

	/**
	 * Constructs a new {@code TokenUserPrivateTool} with the specified dependencies.
	 *
	 * @param tokenTools            Utility for decoding and verifying tokens.
	 * @param userPrivateRepository Repository for accessing user private data.
	 */
	public TokenUserPrivateTool(KnowyTokenTools tokenTools, UserPrivateRepository userPrivateRepository) {
		this.tokenTools = tokenTools;
		this.userPrivateRepository = userPrivateRepository;
	}

	/**
	 * Verifies the provided password reset token and returns the associated user.
	 * <p>
	 * Decodes the token without verification to extract user ID, retrieves the user, then verifies the token against
	 * the user's current password.
	 *
	 * @param token The password reset token to verify.
	 * @return The {@link UserPrivate} entity associated with the token.
	 * @throws NullPointerException       If the token is null.
	 * @throws KnowyTokenException        If the token is invalid or tampered.
	 * @throws KnowyDataAccessException   If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException If no user is found for the token's user ID.
	 */
	public UserPrivate verifyPasswordToken(String token) throws KnowyTokenException, KnowyDataAccessException {
		Objects.requireNonNull(token, "A not null token is required");

		PasswordResetInfo passwordResetInfo = tokenTools.decodeUnverified(token, PasswordResetInfo.class);
		UserPrivate userPrivate = getUserPrivateByIdOrThrow(passwordResetInfo.userId());
		tokenTools.decode(userPrivate.password().value(), token, PasswordResetInfo.class);
		return userPrivate;
	}

	/**
	 * Validates the provided password reset token.
	 *
	 * @param token the token to validate; must not be {@code null}
	 * @return {@code true} if the token is valid and linked to a user; {@code false} otherwise
	 */
	public boolean isValidToken(String token) {
		try {
			verifyPasswordToken(token);
			return true;
		} catch (KnowyTokenException | KnowyDataAccessException e) {
			return false;
		}
	}

	private UserPrivate getUserPrivateByIdOrThrow(int userId) throws KnowyDataAccessException {
		return userPrivateRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
	}

	/**
	 * Creates a password reset token for the user identified by the given email, with a specified expiration time.
	 *
	 * @param email               the user's email address
	 * @param tokenExpirationTime the token expiration time in milliseconds
	 * @return the encoded password reset token
	 * @throws KnowyTokenException        if token creation fails
	 * @throws KnowyDataAccessException   If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException if no user is found with the given email
	 */
	public String createUserTokenByEmail(Email email, long tokenExpirationTime)
		throws KnowyDataAccessException, KnowyTokenException {
		UserPrivate userPrivate = getUserPrivateByEmailOrThrow(email);

		PasswordResetInfo passwordResetInfo = new PasswordResetInfo(userPrivate.id(), userPrivate.email().value());
		return tokenTools.encode(passwordResetInfo, userPrivate.password().value(), tokenExpirationTime);
	}

	/**
	 * Creates a password reset token for the user identified by the given email, with a default expiration time of 10
	 * minutes (600,000 milliseconds).
	 *
	 * @param email the user's email address
	 * @return the encoded password reset token
	 * @throws KnowyTokenException        if token creation fails
	 * @throws KnowyDataAccessException   If an error occurs during the persistence or retrieval of data.
	 * @throws KnowyUserNotFoundException if no user is found with the given email
	 */
	public String createUserTokenByEmail(Email email) throws KnowyDataAccessException, KnowyTokenException {
		return createUserTokenByEmail(email, 600_000);
	}

	private UserPrivate getUserPrivateByEmailOrThrow(Email email) throws KnowyDataAccessException {
		return userPrivateRepository.findByEmail(email.value())
			.orElseThrow(() -> new KnowyUserNotFoundException(String.format("The user with email %s was not found", email)));
	}
}
