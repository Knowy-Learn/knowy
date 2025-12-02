package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyValidationException;

public class KnowyNicknameAlreadyTakenException extends KnowyValidationException {
	public KnowyNicknameAlreadyTakenException(String message) {
		super(message);
	}
}
