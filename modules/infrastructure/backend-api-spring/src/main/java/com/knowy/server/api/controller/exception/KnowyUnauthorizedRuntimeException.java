package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

/**
 * Exception thrown when authentication is required and has failed or has not been provided.
 * Maps to an HTTP 401 Unauthorized response.
 */
public class KnowyUnauthorizedRuntimeException extends KnowyRuntimeException {
	/**
	 * @param message Detailed error message.
	 */
	public KnowyUnauthorizedRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message Detailed error message.
	 * @param cause Underlying cause of the exception.
	 */
	public KnowyUnauthorizedRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
