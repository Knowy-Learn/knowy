package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyUnauthorizedRuntimeException extends KnowyRuntimeException {
	public KnowyUnauthorizedRuntimeException(String message) {
		super(message);
	}

	public KnowyUnauthorizedRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
