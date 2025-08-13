package com.knowy.server.domain;

import com.knowy.server.domain.exception.KnowyPasswordFormatException;
import com.knowy.server.domain.exception.KnowyPasswordFormatRuntimeException;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

public record Password(String value) implements Serializable {

	public Password {
		Objects.requireNonNull(value, "A plain password value can't be null");
		if (!Password.isRightPasswordFormat(value)) {
			throw new KnowyPasswordFormatRuntimeException("Invalid password format");
		}
	}

	public static void assertPasswordFormatIsRight(String password) throws KnowyPasswordFormatException {
		if (!isRightPasswordFormat(password)) {
			throw new KnowyPasswordFormatException("Invalid password format");
		}
	}

	public static boolean isRightPasswordFormat(String password) {
		String regex = "^(?=.*\\d)(?=.*[!-/:-@])(?=.*[A-Z])(?=.*[a-z])\\S{8,}$";
		return Pattern.matches(regex, password);
	}
}