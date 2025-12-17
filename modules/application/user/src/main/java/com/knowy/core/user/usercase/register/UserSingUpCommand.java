package com.knowy.core.user.usercase.register;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Gender;
import com.knowy.core.user.domain.Password;

public record UserSingUpCommand(String nickname, Gender gender, Email email, Password password) {

	/**
	 * @deprecated since 1.2, Implemented gender on user values
	 */
	@Deprecated(since = "1.2")
	public UserSingUpCommand(String nickname, String email, String password) {
		this(nickname, Gender.UNKNOWN, new Email(email), new Password(password));
	}
}
