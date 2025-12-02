package com.knowy.core.exception;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyUserLessonNotFoundException extends KnowyInconsistentDataException {
	public KnowyUserLessonNotFoundException(String message) {
		super(message);
	}
}
