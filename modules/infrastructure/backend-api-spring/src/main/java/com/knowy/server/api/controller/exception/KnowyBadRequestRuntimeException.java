package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

/**
 * Exception thrown when a request contains invalid data or parameters.
 * Maps to an HTTP 400 Bad Request response.
 */
public class KnowyBadRequestRuntimeException extends KnowyRuntimeException {
	/**
	 * @param message Detailed error message.
	 */
	public KnowyBadRequestRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message Detailed error message.
	 * @param cause Underlying cause of the exception.
	 */
	public KnowyBadRequestRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
