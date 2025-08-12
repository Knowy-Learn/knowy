package com.knowy.server.application.usecase.manage;

import com.knowy.server.domain.Email;
import com.knowy.server.domain.Password;

public record DeactivateAccountCommand(
	Email email,
	Password password,
	Password confirmPassword,
	String recoveryBaseUrl
) {
}
