package com.knowy.server.api.exception;

public class KnowyInternalServerErrorException extends RuntimeException {

	public KnowyInternalServerErrorException(String message) {
		super(message);
	}

	public KnowyInternalServerErrorException(String message, Throwable cause) {
		super(message, cause);
	}
}