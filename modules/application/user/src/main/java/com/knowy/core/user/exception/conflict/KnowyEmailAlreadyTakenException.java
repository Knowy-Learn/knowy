package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.KnowyException;

public class KnowyEmailAlreadyTakenException extends KnowyException {
	public KnowyEmailAlreadyTakenException(String message) {
		super(message);
	}
}
