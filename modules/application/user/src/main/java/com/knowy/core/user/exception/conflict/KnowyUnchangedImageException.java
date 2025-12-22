package com.knowy.core.user.exception.conflict;

import com.knowy.core.exception.validation.KnowyConflictException;

public class KnowyUnchangedImageException extends KnowyConflictException {
	public KnowyUnchangedImageException(String message) {
		super(message);
	}
}
