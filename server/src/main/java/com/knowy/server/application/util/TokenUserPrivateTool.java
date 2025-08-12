package com.knowy.server.application.util;

import com.knowy.server.application.exception.KnowyTokenException;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyUserNotFoundException;
import com.knowy.server.application.model.PasswordResetInfo;
import com.knowy.server.application.ports.KnowyTokenTools;
import com.knowy.server.application.ports.UserPrivateRepository;
import com.knowy.server.domain.UserPrivate;

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
	 * Validates the provided password reset token.
	 *
	 * @param token the token to validate; must not be {@code null}
	 * @return {@code true} if the token is valid and linked to a user; {@code false} otherwise
	 */
	public boolean isValidToken(String token) {
		try {
			verifyPasswordToken(token);
			return true;
		} catch (KnowyTokenException | KnowyUserNotFoundException e) {
			return false;
		}
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
	 * @throws KnowyUserNotFoundException If no user is found for the token's user ID.
	 */
	public UserPrivate verifyPasswordToken(String token) throws KnowyTokenException, KnowyUserNotFoundException {
		Objects.requireNonNull(token, "A not null token is required");

		PasswordResetInfo passwordResetInfo = tokenTools.decodeUnverified(token, PasswordResetInfo.class);
		UserPrivate userPrivate = getUserPrivateByIdOrThrow(passwordResetInfo.userId());
		tokenTools.decode(userPrivate.password().value(), token, PasswordResetInfo.class);
		return userPrivate;
	}

	private UserPrivate getUserPrivateByIdOrThrow(int userId) throws KnowyUserNotFoundException {
		return userPrivateRepository.findById(userId)
			.orElseThrow(() -> new KnowyUserNotFoundException("User not found"));
	}
}
