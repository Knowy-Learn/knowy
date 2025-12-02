package com.knowy.core.exception.data;

public class KnowyInconsistentDataException extends KnowyDataAccessException {
	public KnowyInconsistentDataException(String message) {
		super(message);
	}

	public KnowyInconsistentDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
