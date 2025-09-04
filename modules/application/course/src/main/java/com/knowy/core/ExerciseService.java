package com.knowy.core;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.port.ExerciseRepository;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.core.usecase.exercise.GetAllUserExercisesByCourseIdAndLessonIdUseCase;
import com.knowy.core.usecase.exercise.GetNextExerciseByLessonIdUseCase;
import com.knowy.core.usecase.exercise.GetNextExerciseByUserIdUseCase;
import com.knowy.core.usecase.exercise.GetUserExerciseByIdOrCreate;

import java.util.List;

public class ExerciseService {

	private final GetNextExerciseByLessonIdUseCase getNextExerciseByLessonIdUseCase;
	private final GetNextExerciseByUserIdUseCase getNextExerciseByUserIdUseCase;
	private final GetUserExerciseByIdOrCreate getUserExerciseByIdOrCreate;
	private final GetAllUserExercisesByCourseIdAndLessonIdUseCase getAllUserExercisesByCourseIdAndLessonIdUseCase;

	/**
	 * Constructs a new {@code ExerciseService} and initializes all underlying use cases.
	 *
	 * @param userExerciseRepository the repository used to manage user exercises
	 * @param exerciseRepository     the repository used to access exercise data
	 */
	public ExerciseService(
		UserExerciseRepository userExerciseRepository,
		ExerciseRepository exerciseRepository
	) {
		this.getNextExerciseByLessonIdUseCase = new GetNextExerciseByLessonIdUseCase(userExerciseRepository);
		this.getNextExerciseByUserIdUseCase = new GetNextExerciseByUserIdUseCase(userExerciseRepository);
		this.getUserExerciseByIdOrCreate = new GetUserExerciseByIdOrCreate(userExerciseRepository, exerciseRepository);
		this.getAllUserExercisesByCourseIdAndLessonIdUseCase = new GetAllUserExercisesByCourseIdAndLessonIdUseCase(userExerciseRepository);
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
	 * Retrieves all exercises associated with a given user and lesson.
	 * <p>
	 * This method delegates to {@link GetAllUserExercisesByCourseIdAndLessonIdUseCase} to fetch the {@link UserExercise} entities. If no
	 * exercises are found, an empty list is returned.
	 * </p>
	 *
	 * @param userId   the ID of the user
	 * @param lessonId the ID of the lesson
	 * @return a list of {@link UserExercise} for the specified user and lesson, or an empty list if none exist
	 * @throws KnowyDataAccessException if an error occurs while accessing the data source
	 */
	public List<UserExercise> getAllUserExerciseByUserIdAndLessonId(int userId, int lessonId) throws KnowyDataAccessException {
		return getAllUserExercisesByCourseIdAndLessonIdUseCase.execute(userId, lessonId);
	}
}
