package com.knowy.core.domain;

/**
 * Identity and Navigation Contract for Lessons.
 * <p>
 * This interface defines the unique reference for a lesson and its relational context within a course sequence.
 */
public interface LessonIdentifier {

	int id();

	int courseId();

	Integer nextLessonId();
}
