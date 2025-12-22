package com.knowy.core.exception.validation;

import com.knowy.core.exception.KnowyException;

public class KnowyInvalidDataException extends KnowyException {

	public KnowyInvalidDataException(String message) {
		super(message);
	}

	public KnowyInvalidDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
