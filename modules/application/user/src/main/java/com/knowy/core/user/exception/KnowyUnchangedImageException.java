package com.knowy.core.user.exception;

import com.knowy.core.exception.KnowyValidationException;

public class KnowyUnchangedImageException extends KnowyValidationException {
	public KnowyUnchangedImageException(String message) {
		super(message);
	}
}
