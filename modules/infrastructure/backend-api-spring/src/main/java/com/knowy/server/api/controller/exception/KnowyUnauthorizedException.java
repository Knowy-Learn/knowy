package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyUnauthorizedException extends KnowyRuntimeException {
	public KnowyUnauthorizedException(String message) {
		super(message);
	}

	public KnowyUnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}
}
