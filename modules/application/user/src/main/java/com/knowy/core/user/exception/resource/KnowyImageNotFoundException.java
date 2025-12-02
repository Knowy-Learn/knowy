package com.knowy.core.user.exception.resource;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyImageNotFoundException extends KnowyInconsistentDataException {
	public KnowyImageNotFoundException(String message) {
		super(message);
	}
}
