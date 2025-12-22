package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyConflictException;

public class KnowyUnchangedNicknameException extends KnowyConflictException {
	public KnowyUnchangedNicknameException(String message) {
		super(message);
	}
}
