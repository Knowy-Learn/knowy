package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.KnowyRuntimeException;

public class KnowyPasswordFormatRuntimeException extends KnowyRuntimeException {
	public KnowyPasswordFormatRuntimeException(String message) {
		super(message);
	}
}
