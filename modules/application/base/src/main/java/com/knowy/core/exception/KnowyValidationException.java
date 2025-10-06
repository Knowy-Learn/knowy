package com.knowy.core.exception;

/**
 * Exception for validation errors as a checked exception.
 */
public class KnowyValidationException extends KnowyException {
	public KnowyValidationException(String message) {
		super(message);
	}

	public KnowyValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
