package com.knowy.core.exception.validation;

import com.knowy.core.exception.KnowyException;

public class KnowyConflictException extends KnowyException {
	public KnowyConflictException(String message) {
		super(message);
	}
}
