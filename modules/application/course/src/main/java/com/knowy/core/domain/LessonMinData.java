package com.knowy.core.domain;

/**
 * Minimal Data Contract for Lessons.
 * <p>
 * This interface isolates the core descriptive attributes of a lesson, independent of its identity or nested content.
 */
public interface LessonMinData {

	String title();

	String explanation();
}
