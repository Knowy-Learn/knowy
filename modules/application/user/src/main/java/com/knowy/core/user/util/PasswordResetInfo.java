package com.knowy.core.user.util;

import com.knowy.core.user.domain.Email;

/**
 * Represents the essential information required to process a password reset request.
 * <p>
 * This record encapsulates the user's unique identifier and their associated email, leveraging the {@link Email} domain
 * class to ensure the reset process is linked to a valid and properly formatted account.
 *
 * @param userId the unique identifier of the user requesting the password reset.
 * @param email  the {@link Email} address associated with the user account.
 */
public record PasswordResetInfo(int userId, Email email) {

	/**
	 * @param userId the unique identifier of the user.
	 * @param email  the email address as a {@code String}.
	 * @deprecated Since 1.2. Use {@link #PasswordResetInfo(int, Email)} to ensure type safety and that the email
	 * follows domain validation rules.
	 */
	@Deprecated(since = "1.2")
	public PasswordResetInfo(int userId, String email) {
		this(userId, new Email(email));
	}
}
