package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyInternalServerErrorException extends KnowyRuntimeException {
	public KnowyInternalServerErrorException(String message) {
		super(message);
	}

	public KnowyInternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}