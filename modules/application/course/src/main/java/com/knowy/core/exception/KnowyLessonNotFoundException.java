package com.knowy.core.exception;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyLessonNotFoundException extends KnowyInconsistentDataException {
	public KnowyLessonNotFoundException(String message) {
		super(message);
	}
}
