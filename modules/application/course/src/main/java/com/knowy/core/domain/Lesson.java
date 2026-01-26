package com.knowy.core.domain;

import java.util.HashSet;
import java.util.Set;

/**
 * Root domain record for a Lesson aggregate.
 * <p>
 * This record serves as the complete identified state of a lesson, managing its content and its position within the
 * course sequence.
 *
 * @param id             The unique identifier of the lesson.
 * @param courseId       The identifier of the parent course.
 * @param nextLessonId   The identifier of the following lesson (nullable for the last lesson).
 * @param title          The lesson's display title.
 * @param explanation    The main instructional content or summary.
 * @param documentations Set of associated documentation resources.
 * @param exercises      Set of associated practical exercises.
 */
public record Lesson(
	int id,
	int courseId,
	Integer nextLessonId,
	String title,
	String explanation,
	Set<Documentation> documentations,
	Set<Exercise> exercises
) implements LessonIdentifier, LessonData<Documentation, Exercise> {

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