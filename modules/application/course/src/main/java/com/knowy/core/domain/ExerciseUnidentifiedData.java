package com.knowy.core.domain;

import java.util.List;
import java.util.Objects;

/**
 * Creation Contract for Exercises (Identity-less).
 * <p>
 * This interface specializes {@link ExerciseData} by locking its components to unidentified types. It is used to handle
 * exercise data that has not yet been persisted or assigned a unique ID.
 */
public interface ExerciseUnidentifiedData extends ExerciseData<OptionUnidentifiedData> {

	/**
	 * Immutable implementation of identity-less exercise data.
	 * <p>
	 * Ensures structural integrity via null checks, serving as a reliable data carrier for new exercises.
	 *
	 * @param statement The question or problem description.
	 * @param options   The list of options without assigned identifiers.
	 */
	record InmutableExerciseData(
		String statement,
		List<OptionUnidentifiedData> options
	) implements ExerciseUnidentifiedData {

		/**
		 * Validates that all exercise components are non-null.
		 *
		 * @throws NullPointerException if any required field is missing.
		 */
		public InmutableExerciseData(String statement, List<OptionUnidentifiedData> options) {
			this.statement = Objects.requireNonNull(statement, "statement cannot be null");
			this.options = Objects.requireNonNull(options, "options cannot be null");
		}
	}
}
