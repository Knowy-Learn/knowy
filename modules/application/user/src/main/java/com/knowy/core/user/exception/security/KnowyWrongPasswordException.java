package com.knowy.core.user.exception.security;

import com.knowy.core.exception.validation.KnowyInvalidDataException;

public class KnowyWrongPasswordException extends KnowyInvalidDataException {
	public KnowyWrongPasswordException(String message) {
		super(message);
	}
}
