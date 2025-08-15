package com.knowy.server.application.exception.data.inconsistent.notfound;

import com.knowy.core.exception.KnowyInconsistentDataException;

public class KnowyCourseNotFound extends KnowyInconsistentDataException {
	public KnowyCourseNotFound(String message) {
		super(message);
	}
}
