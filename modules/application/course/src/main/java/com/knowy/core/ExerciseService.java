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
import com.knowy.core.usecase.exercise.ProcessUserExerciseAnswerUseCase;

import java.util.Optional;

public class ExerciseService {

	private final UserExerciseRepository userExerciseRepository;
	private final GetNextExerciseByLessonIdUseCase getNextExerciseByLessonIdUseCase;
	private final GetNextExerciseByUserIdUseCase getNextExerciseByUserIdUseCase;
	private final GetUserExerciseByIdOrCreate getUserExerciseByIdOrCreate;
	private final ProcessUserExerciseAnswerUseCase processUserExerciseAnswerUseCase;

	/**
	 * The constructor
	 *
	 * @param userExerciseRepository the publicUserExerciseRepository
	 * @param exerciseRepository     the exerciseRepository
	 */
	public ExerciseService(UserExerciseRepository userExerciseRepository, ExerciseRepository exerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;

		this.getNextExerciseByLessonIdUseCase = new GetNextExerciseByLessonIdUseCase(userExerciseRepository);
		this.getNextExerciseByUserIdUseCase = new GetNextExerciseByUserIdUseCase(userExerciseRepository);
		this.getUserExerciseByIdOrCreate = new GetUserExerciseByIdOrCreate(userExerciseRepository, exerciseRepository);
		this.processUserExerciseAnswerUseCase = new ProcessUserExerciseAnswerUseCase(userExerciseRepository);
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

	// TODO - Move To LessonService
	public Optional<Double> findAverageRateByLessonId(int lessonId) throws KnowyDataAccessException {
		return userExerciseRepository.findAverageRateByLessonId(lessonId);
	}

	// TODO - Move To LessonService
	public double getAverageRateByLessonId(int lessonId) throws KnowyDataAccessException {
		return findAverageRateByLessonId(lessonId)
			.orElseThrow(() -> new KnowyExerciseNotFoundException(
				"No average rate found for lesson ID " + lessonId));
	}

	/**
	 * Processes a user's answer for a given exercise by delegating to the {@link ProcessUserExerciseAnswerUseCase}.
	 *
	 * <p>This method updates the {@link UserExercise} based on the difficulty of the user's answer
	 * and schedules the next review accordingly.</p>
	 *
	 * @param exerciseDifficult the difficulty of the user's answer (EASY, MEDIUM, HARD, FAIL)
	 * @param userExercise      the {@link UserExercise} to update
	 * @throws KnowyDataAccessException if an error occurs while saving the updated exercise
	 */
	public void processUserAnswer(ExerciseDifficult exerciseDifficult, UserExercise userExercise) throws KnowyDataAccessException {
		processUserExerciseAnswerUseCase.execute(exerciseDifficult, userExercise);
	}
}
