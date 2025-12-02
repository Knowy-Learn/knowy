package com.knowy.core.user.exception.security;

import com.knowy.core.exception.KnowyException;

public class KnowyTokenException extends KnowyException {
	public KnowyTokenException(String message) {
		super(message);
	}

	public KnowyTokenException(String message, Throwable cause) {
		super(message, cause);
	}
}
