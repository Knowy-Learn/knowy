package com.knowy.core.user.exception.security;

import com.knowy.core.user.exception.validation.KnowyInvalidUserException;

public class KnowyWrongPasswordException extends KnowyInvalidUserException {
	public KnowyWrongPasswordException(String message) {
		super(message);
	}
}
