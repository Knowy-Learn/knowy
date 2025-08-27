package com.knowy.core.usecase.exercise;

import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.port.UserExerciseRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Use case for processing a user's answer to an exercise and updating the user's exercise progress.
 *
 * <p>This use case adjusts the user's exercise rate and schedules the next review based on the difficulty
 * of the answer (EASY, MEDIUM, HARD, FAIL). It then persists the updated {@link UserExercise} using the
 * {@link UserExerciseRepository}.</p>
 */
public class ProcessUserExerciseAnswerUseCase {

	private final UserExerciseRepository userExerciseRepository;

	/**
	 * Constructs a new {@code ProcessUserExerciseAnswerUseCase} with the specified repository.
	 *
	 * @param userExerciseRepository the repository used to save updated user exercises
	 */
	public ProcessUserExerciseAnswerUseCase(UserExerciseRepository userExerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
	}

	/**
	 * Processes a user's answer for a given exercise and updates the exercise progress.
	 *
	 * <p>The update logic varies depending on the difficulty of the user's response:
	 * <ul>
	 *     <li>EASY: increases rate significantly and schedules next review accordingly</li>
	 *     <li>MEDIUM: increases rate moderately</li>
	 *     <li>HARD: decreases rate slightly</li>
	 *     <li>FAIL: decreases rate significantly</li>
	 * </ul>
	 * </p>
	 *
	 * @param exerciseDifficult the difficulty of the user's answer
	 * @param userExercise      the {@link UserExercise} to update
	 * @throws KnowyDataAccessException if an error occurs while saving the updated user exercise
	 * @throws NullPointerException     if either {@code exerciseDifficult} or {@code userExercise} is null
	 */
	public void execute(ExerciseDifficult exerciseDifficult, UserExercise userExercise) throws KnowyDataAccessException {
		UserExercise updatedUserExercise = difficultSelect(exerciseDifficult, userExercise);
		userExerciseRepository.save(updatedUserExercise);
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
