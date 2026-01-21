package com.knowy.core.exception.data;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyDataAccessRuntimeException extends KnowyRuntimeException {
	public KnowyDataAccessRuntimeException(String message) {
		super(message);
	}

	public KnowyDataAccessRuntimeException(Throwable cause) {
		super(cause);
	}

	public KnowyDataAccessRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
