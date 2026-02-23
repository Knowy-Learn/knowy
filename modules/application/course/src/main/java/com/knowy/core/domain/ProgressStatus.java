package com.knowy.core.domain;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;

public enum ProgressStatus {
	COMPLETED,
	IN_PROGRESS,
	NOT_STARTED;

	public ProgressStatus getNextStatus() {
		return switch (this) {
			case NOT_STARTED -> IN_PROGRESS;
			case IN_PROGRESS, COMPLETED -> COMPLETED;
		};
	}

	public static ProgressStatus fromString(String value) {
		if (value == null) {
			throw new KnowyIllegalArgumentRuntimeException("Status cannot be null");
		}

		return switch (value.trim().toUpperCase()) {
			case "COMPLETED" -> COMPLETED;
			case "IN_PROGRESS" -> IN_PROGRESS;
			case "NOT_STARTED" -> NOT_STARTED;
			default -> throw new KnowyIllegalArgumentRuntimeException("Unknown status: " + value);
		};
	}
}
