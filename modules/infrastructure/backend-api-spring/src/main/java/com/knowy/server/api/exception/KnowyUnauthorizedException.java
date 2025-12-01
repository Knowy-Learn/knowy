package com.knowy.server.api.exception;

public class KnowyUnauthorizedException extends RuntimeException {
	public KnowyUnauthorizedException(String message) {
		super(message);
	}

	public KnowyUnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}
}
