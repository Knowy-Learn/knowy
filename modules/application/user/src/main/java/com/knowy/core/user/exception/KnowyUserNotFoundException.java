package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyInconsistentDataException;

public class KnowyUserNotFoundException extends KnowyInconsistentDataException {
	public KnowyUserNotFoundException(String message) {
		super(message);
	}
}
