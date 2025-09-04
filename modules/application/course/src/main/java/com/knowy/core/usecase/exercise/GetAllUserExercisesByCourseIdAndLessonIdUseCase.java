package com.knowy.core.usecase.exercise;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.port.UserExerciseRepository;

import java.util.List;

/**
 * Use case for retrieving all exercises assigned to a user within a specific lesson.
 * <p>
 * This use case delegates to the {@link UserExerciseRepository} to obtain all {@link UserExercise} entities that belong
 * to the given user and lesson.
 * </p>
 */
public class GetAllUserExercisesByCourseIdAndLessonIdUseCase {

	private final UserExerciseRepository userExerciseRepository;

	/**
	 * Constructs a new {@code GetAllUserExercisesByIdUseCase} with the specified repository.
	 *
	 * @param userExerciseRepository the repository used to access user exercises
	 */
	public GetAllUserExercisesByCourseIdAndLessonIdUseCase(UserExerciseRepository userExerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
	}

	/**
	 * Retrieves all {@link UserExercise} entities for a given user and lesson.
	 *
	 * @param userId   the ID of the user
	 * @param lessonId the ID of the lesson
	 * @return a list of {@link UserExercise} belonging to the user in the given lesson
	 * @throws KnowyDataAccessException if an error occurs while accessing the repository
	 */
	public List<UserExercise> execute(int userId, int lessonId) throws KnowyDataAccessException {
		return userExerciseRepository.findAllByUserIdAndLessonId(userId, lessonId);
	}
}
