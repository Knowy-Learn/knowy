package com.knowy.server.application.exception.data.inconsistent.notfound;

import com.knowy.core.exception.KnowyInconsistentDataException;

public class KnowyLessonNotFoundException extends KnowyInconsistentDataException {
	public KnowyLessonNotFoundException(String message) {
		super(message);
	}
}
