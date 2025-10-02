package com.knowy.core.domain;

import java.util.List;

public interface ExerciseData<O extends OptionData> {

	String statement();

	List<O> options();

	record InmutableExerciseData(String statement, List<OptionData> options) implements ExerciseData<OptionData> {
		InmutableExerciseData(ExerciseData<OptionData> exerciseData) {
			this(exerciseData.statement(), exerciseData.options());
		}
	}
}
