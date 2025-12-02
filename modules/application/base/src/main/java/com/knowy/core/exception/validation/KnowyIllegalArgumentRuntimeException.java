package com.knowy.core.exception.validation;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyIllegalArgumentRuntimeException extends KnowyRuntimeException {
	public KnowyIllegalArgumentRuntimeException(String message) {
		super(message);
	}

	public KnowyIllegalArgumentRuntimeException(Throwable cause) {
		super(cause);
	}
}
