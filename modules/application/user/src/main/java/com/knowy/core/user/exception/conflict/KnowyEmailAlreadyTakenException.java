package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyConflictException;

public class KnowyEmailAlreadyTakenException extends KnowyConflictException {
	public KnowyEmailAlreadyTakenException(String message) {
		super(message);
	}
}
