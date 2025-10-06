package com.knowy.core.domain;

import java.util.List;
import java.util.Objects;

public interface ExerciseData<O extends OptionData> {

	String statement();

	List<O> options();

	record InmutableExerciseData(String statement, List<OptionData> options) implements ExerciseUnidentifiedData {
		public InmutableExerciseData(String statement, List<OptionData> options) {
			this.statement = Objects.requireNonNull(statement, "statement cannot be null");
			this.options = Objects.requireNonNull(options, "options cannot be null");
		}
	}
}