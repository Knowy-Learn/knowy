package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyValidationException;

public class KnowyUnchangedEmailException extends KnowyValidationException {
	public KnowyUnchangedEmailException(String message) {
		super(message);
	}
}
