package com.knowy.core.exception.data;

public class KnowyInconsistentDataRuntimeException extends KnowyDataAccessRuntimeException {
	public KnowyInconsistentDataRuntimeException(String message) {
		super(message);
	}

	public KnowyInconsistentDataRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
