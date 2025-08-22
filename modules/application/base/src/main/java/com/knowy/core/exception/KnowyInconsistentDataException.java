package com.knowy.core.exception;

public class KnowyInconsistentDataException extends KnowyDataAccessException {
	public KnowyInconsistentDataException(String message) {
		super(message);
	}

	public KnowyInconsistentDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
