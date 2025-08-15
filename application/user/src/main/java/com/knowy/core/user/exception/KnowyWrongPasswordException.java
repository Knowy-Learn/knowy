package com.knowy.core.user.exception;

public class KnowyWrongPasswordException extends KnowyInvalidUserException {
	public KnowyWrongPasswordException(String message) {
		super(message);
	}
}
