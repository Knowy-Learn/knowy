package com.knowy.core.domain;

/**
 * Summary record for Lesson information.
 * <p>
 * Combines identity, sequence tracking, and descriptive metadata. This is the ideal representation for list views,
 * navigation menus, or search results where the full lesson content is not required.
 *
 * @param id           The unique identifier of the lesson.
 * @param courseId     The identifier of the parent course.
 * @param nextLessonId The identifier of the following lesson in the sequence.
 * @param title        The display title of the lesson.
 * @param explanation  A brief summary or instructional text.
 */
public record LessonInfo(
	int id,
	int courseId,
	Integer nextLessonId,
	String title,
	String explanation
) implements LessonIdentifier, LessonMinData {
}
