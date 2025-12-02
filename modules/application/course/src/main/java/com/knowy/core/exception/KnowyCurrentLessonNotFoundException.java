package com.knowy.core.exception;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyCurrentLessonNotFoundException extends KnowyInconsistentDataException {
	public KnowyCurrentLessonNotFoundException(String message) {
		super(message);
	}
}
