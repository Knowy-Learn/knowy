package com.knowy.core.usecase.adjust;

import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.port.UserExerciseRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Use case for adjusting a user's exercise progress based on their survey response (difficulty rating).
 *
 * <p>This use case modifies the user's exercise rate and schedules the next review time
 * according to the difficulty level selected (EASY, MEDIUM, HARD, FAIL). The updated {@link UserExercise} is then
 * persisted via the {@link UserExerciseRepository}.</p>
 */
class AdjustExerciseToSurveyResponseUseCase {

	private final UserExerciseRepository userExerciseRepository;

	/**
	 * Constructs a new {@code AdjustExerciseToSurveyResponseUseCase} with the specified repository.
	 *
	 * @param userExerciseRepository the repository used to persist updated user exercises
	 */
	public AdjustExerciseToSurveyResponseUseCase(UserExerciseRepository userExerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
	}

	/**
	 * Processes a user's answer for a given exercise and updates their progress accordingly.
	 *
	 * <p>Depending on the difficulty of the response:
	 * <ul>
	 *     <li><b>EASY</b>: increases the rate significantly and schedules a longer review interval</li>
	 *     <li><b>MEDIUM</b>: increases the rate moderately</li>
	 *     <li><b>HARD</b>: decreases the rate slightly</li>
	 *     <li><b>FAIL</b>: decreases the rate significantly with a very short review interval</li>
	 * </ul>
	 * </p>
	 *
	 * @param exerciseDifficult the difficulty selected by the user
	 * @param userExercise      the {@link UserExercise} to be updated
	 * @return the updated and persisted {@link UserExercise}
	 * @throws KnowyDataAccessException if an error occurs while saving the updated user exercise
	 * @throws NullPointerException     if {@code exerciseDifficult} or {@code userExercise} is {@code null}
	 */
	public UserExercise execute(ExerciseDifficult exerciseDifficult, UserExercise userExercise) throws KnowyDataAccessException {
		UserExercise updatedUserExercise = difficultSelect(exerciseDifficult, userExercise);
		return userExerciseRepository.save(updatedUserExercise);
	}

	private UserExercise difficultSelect(ExerciseDifficult exerciseDifficult, UserExercise userExercise) {
		Objects.requireNonNull(exerciseDifficult, "exerciseDifficult cannot be null");
		Objects.requireNonNull(userExercise, "publicUserExerciseEntity cannot be null");

		return switch (exerciseDifficult) {
			case EASY -> easySelect(userExercise);
			case MEDIUM -> mediumSelect(userExercise);
			case HARD -> hardSelect(userExercise);
			case FAIL -> failSelect(userExercise);
		};
	}

	private UserExercise easySelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() + 45;

		LocalDateTime updatedNextReview = LocalDateTime.now().plus(userExercise.rate() >= 90 ? Duration.ofDays(1) : Duration.ofMinutes(15));

		return createUpdatedUserExercise(userExercise, updatedRate, updatedNextReview);
	}

	private UserExercise mediumSelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() + 20;

		LocalDateTime updatedNextReview = LocalDateTime.now().plus(userExercise.rate() >= 90 ? Duration.ofDays(1) : Duration.ofMinutes(7));

		return createUpdatedUserExercise(userExercise, updatedRate, updatedNextReview);
	}

	private UserExercise hardSelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() - 15;
		LocalDateTime updatedNextReview = LocalDateTime.now().plusMinutes(5);

		return createUpdatedUserExercise(userExercise, updatedRate, updatedNextReview);
	}

	private UserExercise failSelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() - 30;
		LocalDateTime updatedNextReview = LocalDateTime.now().plusMinutes(1);

		return createUpdatedUserExercise(userExercise, updatedRate, updatedNextReview);
	}

	private UserExercise createUpdatedUserExercise(UserExercise userExercise, int updatedRate, LocalDateTime updatedNextReview) {
		return new UserExercise(userExercise.userId(), userExercise.exercise(), updatedRate, updatedNextReview);
	}
}
