package com.knowy.core.domain;

import java.util.List;
import java.util.Objects;

public interface ExerciseUnidentifiedData extends ExerciseData<OptionUnidentifiedData> {
	record InmutableExerciseData(String statement, List<OptionUnidentifiedData> options) implements ExerciseUnidentifiedData {
		public InmutableExerciseData(String statement, List<OptionUnidentifiedData> options) {
			this.statement = Objects.requireNonNull(statement, "statement cannot be null");
			this.options = Objects.requireNonNull(options, "options cannot be null");
		}
	}
}
