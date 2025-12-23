package com.knowy.core.user.usercase.update.email;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;

/**
 * Command containing the data required to update a user's email address.
 * <p>
 * This record encapsulates the user identification and the security credentials needed to authorize and perform the
 * email change operation using domain-specific types for enhanced validation.
 *
 * @param userId   the unique identifier of the user whose email is being updated.
 * @param email    the new {@link Email} address to be associated with the account.
 * @param password the current {@link Password} required to verify the user's identity.
 */
public record UserUpdateEmailCommand(int userId, Email email, Password password) {

	/**
	 * @param userId   the unique identifier of the user.
	 * @param email    the new email address as a {@code String}.
	 * @param password the current password as a {@code String}.
	 * @deprecated Since 1.2. Use {@link #UserUpdateEmailCommand(int, Email, Password)} to ensure type safety and early
	 * validation of credentials.
	 */
	@Deprecated(since = "1.2")
	public UserUpdateEmailCommand(int userId, String email, String password) {
		this(userId, new Email(email), new Password(password));
	}
}
