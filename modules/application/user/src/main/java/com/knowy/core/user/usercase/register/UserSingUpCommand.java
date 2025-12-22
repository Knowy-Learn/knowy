package com.knowy.core.user.usercase.register;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Gender;
import com.knowy.core.user.domain.Password;

/**
 * Command containing the required information to register a new user in the system.
 * <p>
 * This record encapsulates the basic profile information and security credentials needed for the sign-up process.
 *
 * @param nickname the unique display name chosen by the user.
 * @param gender   the gender identity of the user.
 * @param email    the verified email address for account communication.
 * @param password the secure password chosen by the user.
 */
public record UserSingUpCommand(String nickname, Gender gender, Email email, Password password) {

	/**
	 * Constructs a new command without an explicit gender. * @param nickname the unique display name.
	 *
	 * @param email    the user's email address as a string.
	 * @param password the user's password as a string.
	 * @deprecated Since 1.2. Gender is now a required field for user profiles. Use
	 * {@link #UserSingUpCommand(String, Gender, Email, Password)} instead to provide full user details.
	 */
	@Deprecated(since = "1.2")
	public UserSingUpCommand(String nickname, String email, String password) {
		this(nickname, Gender.UNKNOWN, new Email(email), new Password(password));
	}
}
