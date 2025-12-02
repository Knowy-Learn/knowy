package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyValidationException;

public class KnowyUnchangedEmailException extends KnowyValidationException {
	public KnowyUnchangedEmailException(String message) {
		super(message);
	}
}
