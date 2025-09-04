package com.knowy.core.domain;

import com.knowy.core.exception.KnowyUnsupportedOperationRuntimeException;

import java.time.LocalDate;
import java.util.List;

public record UserLesson(
	int userId,
	Lesson lesson,
	LocalDate startDate,
	ProgressStatus status
) {

	/**
	 * Calculates the progress of a lesson based on the rates of the provided user exercises.
	 * <p>
	 * The progress is computed as the average of all {@link UserExercise#rate()} values, normalized to a range between
	 * {@code 0.0} and {@code 1.0}.
	 * </p>
	 *
	 * @param userExercises the list of {@link UserExercise} instances associated with the lesson
	 * @return the lesson progress as a double between {@code 0.0} (no progress) and {@code 1.0} (fully completed)
	 * @throws KnowyUnsupportedOperationRuntimeException if the provided list is empty and progress cannot be
	 *                                                   calculated
	 */
	public static double calculateLessonProgressById(List<UserExercise> userExercises) {
		double averageRate = userExercises.stream()
			.mapToInt(UserExercise::rate)
			.average()
			.orElseThrow(() -> new KnowyUnsupportedOperationRuntimeException(
				"Cannot calculate progress: no exercises provided"
			));
		return averageRate / 100;
	}

	public enum ProgressStatus {
		PENDING,
		IN_PROGRESS,
		COMPLETED;

		public ProgressStatus getNextStatus() {
			return switch (this) {
				case PENDING -> IN_PROGRESS;
				case IN_PROGRESS, COMPLETED -> COMPLETED;
			};
		}
	}
}
