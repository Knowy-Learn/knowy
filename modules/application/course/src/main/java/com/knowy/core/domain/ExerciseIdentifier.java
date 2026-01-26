package com.knowy.core.domain;

/**
 * Identity Contract for Exercises.
 * <p>
 * Used for lightweight referencing within the domain or persistence layers.
 */
public interface ExerciseIdentifier {

	int id();

	int lessonId();
}
