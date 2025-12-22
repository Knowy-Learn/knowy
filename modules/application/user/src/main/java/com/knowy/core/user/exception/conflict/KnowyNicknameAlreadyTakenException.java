package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyConflictException;

public class KnowyNicknameAlreadyTakenException extends KnowyConflictException {
	public KnowyNicknameAlreadyTakenException(String message) {
		super(message);
	}
}
