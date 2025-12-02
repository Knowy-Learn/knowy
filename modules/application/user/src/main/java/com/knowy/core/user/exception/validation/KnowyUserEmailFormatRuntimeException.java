package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyUserEmailFormatRuntimeException extends KnowyRuntimeException {
	public KnowyUserEmailFormatRuntimeException(String message) {
		super(message);
	}
}
