package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyValidationException;

public class KnowyNicknameAlreadyTakenException extends KnowyValidationException {
	public KnowyNicknameAlreadyTakenException(String message) {
		super(message);
	}
}
