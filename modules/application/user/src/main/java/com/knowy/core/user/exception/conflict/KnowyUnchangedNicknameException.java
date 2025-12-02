package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyValidationException;

public class KnowyUnchangedNicknameException extends KnowyValidationException {
	public KnowyUnchangedNicknameException(String message) {
		super(message);
	}
}
