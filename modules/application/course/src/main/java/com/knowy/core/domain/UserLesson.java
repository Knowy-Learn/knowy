package com.knowy.core.domain;

import java.time.LocalDate;

public record UserLesson(
	int userId,
	Lesson lesson,
	LocalDate startDate,
	ProgressStatus status
) {

	public enum ProgressStatus {
		PENDING,
		IN_PROGRESS,
		COMPLETED;

		public ProgressStatus getNextStatus() {
			return switch (this) {
				case PENDING -> IN_PROGRESS;
				case IN_PROGRESS, COMPLETED -> COMPLETED;
			};
		}
	}
}
