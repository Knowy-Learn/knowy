package com.knowy.core.exception.data;

import com.knowy.core.exception.KnowyException;

public class KnowyDataAccessException extends KnowyException {
	public KnowyDataAccessException(String message) {
		super(message);
	}

	public KnowyDataAccessException(Throwable cause) {
		super(cause);
	}

	public KnowyDataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
