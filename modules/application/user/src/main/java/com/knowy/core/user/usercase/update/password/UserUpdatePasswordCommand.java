package com.knowy.core.user.usercase.update.password;

import com.knowy.core.user.domain.Password;

/**
 * Command containing the data required to update a user's password using a security token.
 * <p>
 * This record encapsulates the reset token and the new credentials, utilizing the {@link Password} domain class to
 * ensure consistent validation and security policies.
 *
 * @param token           the unique security token used to authorize the password reset.
 * @param password        the new {@link Password} to be established for the account.
 * @param confirmPassword the confirmation {@link Password} to prevent accidental mismatch.
 */
public record UserUpdatePasswordCommand(String token, Password password, Password confirmPassword) {

	/**
	 * @param token           the security token.
	 * @param password        the new password as a {@code String}.
	 * @param confirmPassword the confirmation password as a {@code String}.
	 * @deprecated Since 1.2. Use {@link #UserUpdatePasswordCommand(String, Password, Password)}
	 * to ensure that password validation rules are applied consistently.
	 */
	@Deprecated(since = "1.2")
	public UserUpdatePasswordCommand(String token, String password, String confirmPassword) {
		this(token, new Password(password), new Password(confirmPassword));
	}
}
