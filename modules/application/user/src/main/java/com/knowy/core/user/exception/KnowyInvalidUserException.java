package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyValidationException;

public class KnowyInvalidUserException extends KnowyValidationException {

	public KnowyInvalidUserException(String message) {
		super(message);
	}
}
