package com.knowy.server.domain;

import com.knowy.server.application.exception.validation.user.KnowyInvalidUserEmailException;

public record Email(String value) {

	public static boolean isValid(String email) {
		return email != null && email.chars()
			.filter(ch -> ch == '@')
			.count() == 1;
	}

	public static void assertValid(String email) throws KnowyInvalidUserEmailException {
		if (!Email.isValid(email)) {
			throw new KnowyInvalidUserEmailException("An email must match the expression <user>@<domain>");
		}
	}
}
