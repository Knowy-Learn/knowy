package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.validation.KnowyValidationException;

public class KnowyPasswordFormatException extends KnowyValidationException {
	public KnowyPasswordFormatException(String message) {
		super(message);
	}
}
