package com.knowy.core.user.usercase.manage;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;

public record DeactivateAccountCommand(
	Email email,
	Password password,
	Password confirmPassword,
	String recoveryBaseUrl
) {
}
