package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyValidationException;

public class KnowyPasswordFormatException extends KnowyValidationException {
	public KnowyPasswordFormatException(String message) {
		super(message);
	}
}
