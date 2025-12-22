package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.validation.KnowyInvalidDataException;

public class KnowyInvalidUserGenderException extends KnowyInvalidDataException {
	public KnowyInvalidUserGenderException(String message) {
		super(message);
	}

	public KnowyInvalidUserGenderException(String message, Throwable cause) {
		super(message, cause);
	}
}
