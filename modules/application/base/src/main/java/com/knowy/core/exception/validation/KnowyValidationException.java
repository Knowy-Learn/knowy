package com.knowy.core.exception.validation;

import com.knowy.core.exception.KnowyException;

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
