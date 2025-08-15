package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyInconsistentDataException;

public class KnowyImageNotFoundException extends KnowyInconsistentDataException {
	public KnowyImageNotFoundException(String message) {
		super(message);
	}
}
