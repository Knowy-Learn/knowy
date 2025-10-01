package com.knowy.core.domain;

import java.util.List;

public interface ExerciseData {

	String statement();

	List<Option> options();

	record InmutableExerciseData(String statement, List<Option> options) implements ExerciseData {

		InmutableExerciseData(ExerciseData exerciseData) {
			this(exerciseData.statement(), exerciseData.options());
		}
	}
}
