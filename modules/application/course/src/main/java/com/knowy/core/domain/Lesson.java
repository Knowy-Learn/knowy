package com.knowy.core.domain;

import java.util.HashSet;
import java.util.Set;

public record Lesson(
	int id,
	int courseId,
	Integer nextLessonId,
	String title,
	String explanation,
	Set<Documentation> documentations,
	Set<Exercise> exercises
) implements LessonIdentifier, LessonData {

	public Lesson(
		int id,
		int courseId,
		Integer nextLessonId,
		String title,
		String explanation
	) {
		this(id, courseId, nextLessonId, title, explanation, new HashSet<>(), new HashSet<>());
	}
}