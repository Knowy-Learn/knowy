package com.knowy.core.exception;

public class KnowyDataAccessException extends KnowyException {
	public KnowyDataAccessException(String message) {
		super(message);
	}

	public KnowyDataAccessException(String message, Throwable cause) {
		super(message, cause);
	}
}
