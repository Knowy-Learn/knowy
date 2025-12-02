package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyConflictRuntimeException extends KnowyRuntimeException {
	public KnowyConflictRuntimeException(String message) {
		super(message);
	}

	public KnowyConflictRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
