package com.knowy.core.exception;

public class KnowyCourseNotFound extends KnowyInconsistentDataException {
	public KnowyCourseNotFound(String message) {
		super(message);
	}

	public KnowyCourseNotFound(String message, Throwable cause) {
		super(message, cause);
	}
}
