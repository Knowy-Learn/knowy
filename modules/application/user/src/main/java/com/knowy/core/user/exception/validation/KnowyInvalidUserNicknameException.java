package com.knowy.core.user.exception.validation;

import com.knowy.core.exception.validation.KnowyInvalidDataException;

public class KnowyInvalidUserNicknameException extends KnowyInvalidDataException {
	public KnowyInvalidUserNicknameException(String message) {
		super(message);
	}
}
