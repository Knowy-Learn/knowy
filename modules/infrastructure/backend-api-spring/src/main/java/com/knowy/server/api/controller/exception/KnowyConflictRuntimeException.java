package com.knowy.server.api.controller.exception;

import com.knowy.core.exception.KnowyRuntimeException;

/**
 * Exception thrown when a request conflicts with the current state of the server.
 * Maps to an HTTP 409 Conflict response.
 */
public class KnowyConflictRuntimeException extends KnowyRuntimeException {
	/**
	 * @param message Detailed error message.
	 */
	public KnowyConflictRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message Detailed error message.
	 * @param cause Underlying cause of the exception.
	 */
	public KnowyConflictRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
