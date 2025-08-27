package com.knowy.core;

import com.knowy.core.domain.ExerciseDifficult;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.port.ExerciseRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.usecase.exercise.GetNextExerciseByLessonIdUseCase;
import com.knowy.core.usecase.exercise.GetNextExerciseByUserIdUseCase;
import com.knowy.core.usecase.exercise.GetUserExerciseByIdOrCreate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ExerciseService {

	private final UserExerciseRepository userExerciseRepository;
	private final ExerciseRepository exerciseRepository;
	private final GetNextExerciseByLessonIdUseCase getNextExerciseByLessonIdUseCase;
	private final GetNextExerciseByUserIdUseCase getNextExerciseByUserIdUseCase;
	private final GetUserExerciseByIdOrCreate getUserExerciseByIdOrCreate;

	/**
	 * The constructor
	 *
	 * @param userExerciseRepository the publicUserExerciseRepository
	 * @param exerciseRepository     the exerciseRepository
	 */
	public ExerciseService(UserExerciseRepository userExerciseRepository, ExerciseRepository exerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
		this.exerciseRepository = exerciseRepository;

		this.getNextExerciseByLessonIdUseCase = new GetNextExerciseByLessonIdUseCase(userExerciseRepository);
		this.getNextExerciseByUserIdUseCase = new GetNextExerciseByUserIdUseCase(userExerciseRepository);
		this.getUserExerciseByIdOrCreate = new GetUserExerciseByIdOrCreate(userExerciseRepository, exerciseRepository);
	}

	/**
	 * Retrieves the next exercise for a user within a specific lesson.
	 * <p>
	 * This method delegates to {@link GetNextExerciseByLessonIdUseCase} to obtain the next {@link UserExercise} that
	 * the user should complete. If no exercise is found, the use case will throw a
	 * {@link KnowyExerciseNotFoundException}.
	 *
	 * @param userId   the ID of the user
	 * @param lessonId the ID of the lesson
	 * @return the next {@link UserExercise} for the given user and lesson
	 * @throws KnowyDataAccessException       if an error occurs while accessing the repository
	 * @throws KnowyExerciseNotFoundException if no next exercise exists for the given user and lesson
	 */
	public UserExercise getNextExerciseByLessonId(int userId, int lessonId) throws KnowyDataAccessException {
		return getNextExerciseByLessonIdUseCase.execute(userId, lessonId);
	}

	/**
	 * Retrieves the next exercise assigned to the specified user, regardless of lesson or course.
	 * <p>
	 * Delegates to {@link GetNextExerciseByUserIdUseCase} to fetch the next pending {@link UserExercise}.
	 *
	 * @param userId the ID of the user whose next exercise should be retrieved
	 * @return the next {@link UserExercise} assigned to the user
	 * @throws KnowyDataAccessException       if an error occurs while accessing the repository
	 * @throws KnowyExerciseNotFoundException if no next exercise exists for the given user
	 */
	public UserExercise getNextExerciseByUserId(int userId) throws KnowyDataAccessException {
		return getNextExerciseByUserIdUseCase.execute(userId);
	}

	/**
	 * Retrieves the {@link UserExercise} for a specific user and exercise.
	 * <p>
	 * If the {@link UserExercise} does not exist, a new one is created with default progress and the current
	 * timestamp.
	 *
	 * @param userId     the ID of the user
	 * @param exerciseId the ID of the exercise
	 * @return the existing or newly created {@link UserExercise}
	 * @throws KnowyDataAccessException       if an error occurs while accessing the repository
	 * @throws KnowyExerciseNotFoundException if the specified exercise does not exist
	 */
	public UserExercise getByIdOrCreate(int userId, int exerciseId) throws KnowyDataAccessException {
		return getUserExerciseByIdOrCreate.execute(userId, exerciseId);
	}

	/**
	 * Finds the average rate (score) for all exercises in a given lesson.
	 *
	 * @param lessonId the ID of the lesson
	 * @return an Optional containing the average rate, or empty if none found
	 */
	public Optional<Double> findAverageRateByLessonId(int lessonId) throws KnowyDataAccessException {
		return userExerciseRepository.findAverageRateByLessonId(lessonId);
	}

	/**
	 * Gets the average rate (score) for all exercises in a given lesson. Throws an exception if no average rate is
	 * found.
	 *
	 * @param lessonId the ID of the lesson
	 * @return the average rate for the lesson
	 * @throws KnowyExerciseNotFoundException if no average rate is found for the lesson
	 */
	public double getAverageRateByLessonId(int lessonId) throws KnowyDataAccessException {
		return findAverageRateByLessonId(lessonId)
			.orElseThrow(() -> new KnowyExerciseNotFoundException(
				"No average rate found for lesson ID " + lessonId));
	}

	/**
	 * Saves or updates a public user exercise entity in the repository.
	 *
	 * @return the persisted entity.
	 * @throws NullPointerException if {@code publicUserExerciseEntity} is {@code null}.
	 */
	public UserExercise save(UserExercise userExercise) throws KnowyDataAccessException {
		Objects.requireNonNull(userExercise, "publicUserExerciseEntity cannot be null");
		return userExerciseRepository.save(userExercise);
	}

	/**
	 * Updates the user's exercise record based on the difficulty of their answer.
	 *
	 * <p>This method adjusts the user's rate and schedules the next review time,
	 * according to the difficulty level provided.
	 * </p>
	 *
	 * @param exerciseDifficult the difficulty level chosen by the user for this exercise
	 * @throws NullPointerException if either parameter is null
	 */
	public void processUserAnswer(ExerciseDifficult exerciseDifficult, UserExercise userExercise) throws KnowyDataAccessException {
		UserExercise updatedUserExercise = difficultSelect(exerciseDifficult, userExercise);
		save(updatedUserExercise);
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

		LocalDateTime updatedNextReview = LocalDateTime.now()
			.plus(userExercise.rate() >= 90 ? Duration.ofDays(1) : Duration.ofMinutes(15));

		return new UserExercise(
			userExercise.userId(),
			userExercise.exercise(),
			updatedRate,
			updatedNextReview
		);
	}

	private UserExercise mediumSelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() + 20;

		LocalDateTime updatedNextReview = LocalDateTime.now()
			.plus(userExercise.rate() >= 90 ? Duration.ofDays(1) : Duration.ofMinutes(7));

		return new UserExercise(
			userExercise.userId(),
			userExercise.exercise(),
			updatedRate,
			updatedNextReview
		);
	}

	private UserExercise hardSelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() - 15;
		LocalDateTime updatedNextReview = LocalDateTime.now().plusMinutes(5);

		return new UserExercise(
			userExercise.userId(),
			userExercise.exercise(),
			updatedRate,
			updatedNextReview
		);
	}

	private UserExercise failSelect(UserExercise userExercise) {
		int updatedRate = userExercise.rate() - 30;
		LocalDateTime updatedNextReview = LocalDateTime.now().plusMinutes(1);

		return new UserExercise(
			userExercise.userId(),
			userExercise.exercise(),
			updatedRate,
			updatedNextReview
		);
	}
}
