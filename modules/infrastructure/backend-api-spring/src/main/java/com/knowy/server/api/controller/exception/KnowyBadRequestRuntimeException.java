package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyBadRequestRuntimeException extends KnowyRuntimeException {
	public KnowyBadRequestRuntimeException(String message) {
		super(message);
	}

	public KnowyBadRequestRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
