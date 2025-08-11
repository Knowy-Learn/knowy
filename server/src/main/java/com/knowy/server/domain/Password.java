package com.knowy.server.domain;

import com.knowy.server.application.exception.validation.user.KnowyPasswordFormatException;

import java.io.Serializable;
import java.util.regex.Pattern;

public record Password(String value) implements Serializable {

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