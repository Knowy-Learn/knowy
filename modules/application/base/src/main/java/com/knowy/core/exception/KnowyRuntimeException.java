package com.knowy.core.exception;

import java.util.UUID;

public class KnowyRuntimeException extends RuntimeException {
	private final UUID exceptionUUID;

	public KnowyRuntimeException(String message) {
		this(message, null);
	}

	public KnowyRuntimeException(Throwable cause) {
		this(null, cause);
	}

	public KnowyRuntimeException(String message, Throwable cause) {
		this(UUID.randomUUID(), message, cause);
	}

	private KnowyRuntimeException(UUID exceptionUUID, String message, Throwable cause) {
		super(
			"Identified exception with uuid %s - %s".formatted(exceptionUUID.toString(), message),
			cause
		);
		this.exceptionUUID = exceptionUUID;
	}

	public UUID getExceptionUUID() {
		return exceptionUUID;
	}
}
