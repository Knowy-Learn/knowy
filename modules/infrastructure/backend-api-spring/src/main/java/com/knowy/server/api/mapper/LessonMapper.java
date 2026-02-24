package com.knowy.server.api.mapper;

import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.ProgressStatus;
import com.knowy.core.domain.UserLesson;
import com.knowy.server.api.dto.LessonDto;
import com.knowy.server.api.dto.LessonStepDto;

import java.util.Objects;
import java.util.stream.Collectors;

public class LessonMapper {

	public LessonStepDto toLessonStepDto(UserLesson userLesson) {
		Objects.requireNonNull(userLesson);

		return new LessonStepDto(
			userLesson.lesson().id(),
			userLesson.lesson().nextLessonId(),
			userLesson.lesson().title(),
			ProgressStatus.COMPLETED.equals(userLesson.status())
		);
	}

	public LessonDto toLessonDto(Lesson lesson) {
		Objects.requireNonNull(lesson);

		var documentationMapper = new DocumentationMapper();

		return new LessonDto(
			lesson.id(),
			lesson.courseId(),
			lesson.nextLessonId(),
			lesson.title(),
			lesson.explanation(),
			lesson.documentations().stream()
				.map(documentationMapper::toDto)
				.collect(Collectors.toSet())
		);
	}
}
