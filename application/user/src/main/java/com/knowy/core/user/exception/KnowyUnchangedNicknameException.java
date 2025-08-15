package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyValidationException;

public class KnowyUnchangedNicknameException extends KnowyValidationException {
	public KnowyUnchangedNicknameException(String message) {
		super(message);
	}
}
