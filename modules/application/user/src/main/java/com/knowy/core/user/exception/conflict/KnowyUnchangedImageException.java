package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyValidationException;

public class KnowyUnchangedImageException extends KnowyValidationException {
	public KnowyUnchangedImageException(String message) {
		super(message);
	}
}
