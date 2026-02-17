package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

/**
 * Exception thrown when an unexpected error occurs on the server side.
 * Maps to an HTTP 500 Internal Server Error response.
 */
public class KnowyInternalServerErrorRuntimeException extends KnowyRuntimeException {
	/**
	 * @param message Detailed error message.
	 */
	public KnowyInternalServerErrorRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message Detailed error message.
	 * @param cause Underlying cause of the exception.
	 */
	public KnowyInternalServerErrorRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}