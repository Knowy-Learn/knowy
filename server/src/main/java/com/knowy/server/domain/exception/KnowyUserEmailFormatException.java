package com.knowy.server.domain.exception;

import com.knowy.server.application.exception.validation.user.KnowyInvalidUserException;

public class KnowyUserEmailFormatException extends KnowyInvalidUserException {
	public KnowyUserEmailFormatException(String message) {
		super(message);
	}
}
