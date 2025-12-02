package com.knowy.core.exception;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyCourseNotFound extends KnowyInconsistentDataException {
	public KnowyCourseNotFound(String message) {
		super(message);
	}

	public KnowyCourseNotFound(String message, Throwable cause) {
		super(message, cause);
	}
}
