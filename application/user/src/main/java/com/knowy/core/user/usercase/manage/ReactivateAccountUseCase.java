package com.knowy.core.user.usercase.manage;

import com.knowy.core.user.exception.KnowyTokenException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.core.user.port.UserPrivateRepository;
import com.knowy.core.user.util.TokenUserPrivateTool;
import com.knowy.core.user.domain.UserPrivate;

/**
 * Use case responsible for reactivating a deactivated user account.
 * <p>
 * Verifies the provided token, and if the user account is currently inactive, it reactivates the account by updating
 * its status to active.
 */
public class ReactivateAccountUseCase {

	private final TokenUserPrivateTool tokenUserPrivateTool;
	private final UserPrivateRepository userPrivateRepository;

	/**
	 * Constructs a new {@code ReactivateAccountUseCase} with the specified dependencies.
	 *
	 * @param tokenUserPrivateTool  Utility for verifying user tokens.
	 * @param userPrivateRepository Repository for managing user data.
	 */
	public ReactivateAccountUseCase(TokenUserPrivateTool tokenUserPrivateTool, UserPrivateRepository userPrivateRepository) {
		this.tokenUserPrivateTool = tokenUserPrivateTool;
		this.userPrivateRepository = userPrivateRepository;
	}

	/**
	 * Executes the account reactivation process using the given token.
	 * <p>
	 * If the token is valid and the user is currently inactive, the account will be marked as active again.
	 *
	 * @param token The recovery token used to verify the user identity.
	 * @throws KnowyTokenException        If the token is invalid or expired.
	 * @throws KnowyUserNotFoundException If no user is associated with the token.
	 */
	public void execute(String token) throws KnowyTokenException, KnowyUserNotFoundException {
		UserPrivate userPrivate = tokenUserPrivateTool.verifyPasswordToken(token);
		if (userPrivate.active()) {
			return;
		}

		UserPrivate newUserPrivate = new UserPrivate(
			userPrivate.cropToUser(),
			userPrivate.email(),
			userPrivate.password(),
			true
		);
		userPrivateRepository.save(newUserPrivate);
	}
}
