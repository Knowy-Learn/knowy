package com.knowy.server.api.mapper;

import com.knowy.core.domain.UserLesson;
import com.knowy.server.api.dto.LessonStepDto;

import java.util.Objects;

public class LessonMapper {

	public LessonStepDto toLessonStepDto(UserLesson userLesson) {
		Objects.requireNonNull(userLesson);

		return new LessonStepDto(
			userLesson.lesson().id(),
			userLesson.lesson().nextLessonId(),
			userLesson.lesson().title(),
			UserLesson.ProgressStatus.COMPLETED.equals(userLesson.status())
		);
	}
}
