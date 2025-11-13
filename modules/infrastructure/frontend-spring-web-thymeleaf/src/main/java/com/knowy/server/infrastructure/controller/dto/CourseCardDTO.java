package com.knowy.server.infrastructure.controller.dto;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record CourseCardDTO(
	Integer id,
	String name,
	String author,
	float progress,
	ActionType action,
	List<String> categories,
	String image,
	LocalDateTime creationDate
) {

	public static CourseCardDTO fromDomain(Course course, float progress, ActionType actionType) {
		Objects.requireNonNull(actionType, "ActionType must not be null");

		return new CourseCardDTO(
			course.id(),
			course.title(),
			course.author(),
			progress,
			actionType,
			course.categories()
				.stream()
				.map(Category::name)
				.toList(),
			course.image(),
			course.creationDate()
		);
	}

	public enum ActionType {
		START,
		ACQUIRE
	}
}
