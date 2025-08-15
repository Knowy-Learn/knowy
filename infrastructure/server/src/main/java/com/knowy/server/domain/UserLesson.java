package com.knowy.server.domain;

import com.knowy.core.user.domain.User;

import java.time.LocalDate;

public record UserLesson(
	User user,
	Lesson lesson,
	LocalDate startDate,
	ProgressStatus status
) {

	public enum ProgressStatus {
		PENDING,
		IN_PROGRESS,
		COMPLETED
	}
}
