package com.knowy.core.exception;

public class KnowyIllegalArgumentRuntimeException extends RuntimeException {
	public KnowyIllegalArgumentRuntimeException(String message) {
		super(message);
	}

	public KnowyIllegalArgumentRuntimeException(Throwable cause) {
		super(cause);
	}
}
