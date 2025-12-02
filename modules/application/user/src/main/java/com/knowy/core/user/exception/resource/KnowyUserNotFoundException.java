package com.knowy.core.user.exception.resource;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyUserNotFoundException extends KnowyInconsistentDataException {
	public KnowyUserNotFoundException(String message) {
		super(message);
	}
}
