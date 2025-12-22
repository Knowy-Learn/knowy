package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyConflictException;

public class KnowyUnchangedEmailException extends KnowyConflictException {
	public KnowyUnchangedEmailException(String message) {
		super(message);
	}
}
