package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.validation.KnowyInvalidDataException;

public class KnowyUserEmailFormatException extends KnowyInvalidDataException {
	public KnowyUserEmailFormatException(String message) {
		super(message);
	}
}
