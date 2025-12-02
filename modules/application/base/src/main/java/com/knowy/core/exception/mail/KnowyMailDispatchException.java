package com.knowy.core.exception.mail;

import com.knowy.core.exception.KnowyException;

public class KnowyMailDispatchException extends KnowyException {
	public KnowyMailDispatchException(String message) {
		super(message);
	}

	public KnowyMailDispatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
