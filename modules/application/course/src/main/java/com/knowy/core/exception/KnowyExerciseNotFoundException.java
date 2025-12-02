package com.knowy.core.exception;

import com.knowy.core.exception.data.KnowyInconsistentDataException;

public class KnowyExerciseNotFoundException extends KnowyInconsistentDataException {
	public KnowyExerciseNotFoundException(String message) {
		super(message);
	}
}
