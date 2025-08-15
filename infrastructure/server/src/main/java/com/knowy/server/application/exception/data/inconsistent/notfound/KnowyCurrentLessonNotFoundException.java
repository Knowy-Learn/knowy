package com.knowy.server.application.exception.data.inconsistent.notfound;

import com.knowy.core.exception.KnowyInconsistentDataException;

public class KnowyCurrentLessonNotFoundException extends KnowyInconsistentDataException {
	public KnowyCurrentLessonNotFoundException(String message) {
		super(message);
	}
}
