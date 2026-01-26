package com.knowy.core.domain;

import java.util.List;

/**
 * Root domain record for an Exercise aggregate.
 * <p>
 * This record bridges the exercise's identity with its full content, including its relationship with a parent lesson
 * and its set of possible answers.
 *
 * @param id        The unique identifier of the exercise.
 * @param lessonId  The identifier of the lesson this exercise belongs to.
 * @param statement The question or problem description.
 * @param options   The list of available options/answers for the exercise.
 */
public record Exercise(
	int id,
	int lessonId,
	String statement,
	List<Option> options
) implements ExerciseIdentifier, ExerciseData<Option> {
}
