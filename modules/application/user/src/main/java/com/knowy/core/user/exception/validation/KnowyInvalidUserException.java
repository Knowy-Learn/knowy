package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.validation.KnowyValidationException;

public class KnowyInvalidUserException extends KnowyValidationException {

	public KnowyInvalidUserException(String message) {
		super(message);
	}
}
