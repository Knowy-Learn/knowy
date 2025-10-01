package com.knowy.core.domain;

import java.util.List;

public record Exercise(
	int id,
	int lessonId,
	String statement,
	List<Option> options
) implements ExerciseIdentifier, ExerciseData {
}
