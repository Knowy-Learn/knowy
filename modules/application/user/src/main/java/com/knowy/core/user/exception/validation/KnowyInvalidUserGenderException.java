package com.knowy.core.user.exception.validation;

public class KnowyInvalidUserGenderException extends Exception {
	public KnowyInvalidUserGenderException(String message) {
		super(message);
	}

	public KnowyInvalidUserGenderException(String message, Throwable cause) {
		super(message, cause);
	}
}
