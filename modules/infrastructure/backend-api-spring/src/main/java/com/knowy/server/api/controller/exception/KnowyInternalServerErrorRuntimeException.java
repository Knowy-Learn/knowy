package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyInternalServerErrorRuntimeException extends KnowyRuntimeException {
	public KnowyInternalServerErrorRuntimeException(String message) {
		super(message);
	}

	public KnowyInternalServerErrorRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}