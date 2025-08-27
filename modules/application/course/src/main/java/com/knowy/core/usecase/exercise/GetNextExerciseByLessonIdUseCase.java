package com.knowy.core.usecase.exercise;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.port.UserExerciseRepository;

/**
 * Use case for retrieving the next exercise assigned to a user within a specific lesson.
 * <p>
 * This use case queries the {@link UserExerciseRepository} to determine the next {@link UserExercise} that the user
 * should complete. If no exercise is found, a {@link KnowyExerciseNotFoundException} is thrown.
 */
public class GetNextExerciseByLessonIdUseCase {

	private final UserExerciseRepository userExerciseRepository;

	/**
	 * Constructs a new {@code GetNextExerciseByLessonIdUseCase} with the specified repository.
	 *
	 * @param userExerciseRepository the repository used to access user exercises
	 */
	public GetNextExerciseByLessonIdUseCase(UserExerciseRepository userExerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
	}


	/**
	 * Retrieves the next exercise for the given user within the specified lesson.
	 *
	 * @param userId   the ID of the user
	 * @param lessonId the ID of the lesson
	 * @return the next {@link UserExercise} for the user in the specified lesson
	 * @throws KnowyDataAccessException       if an error occurs while accessing the repository
	 * @throws KnowyExerciseNotFoundException if no next exercise exists for the given user and lesson
	 */
	public UserExercise execute(int userId, int lessonId) throws KnowyDataAccessException {
		return userExerciseRepository.findNextExerciseByLessonId(userId, lessonId)
			.orElseThrow(() -> new KnowyExerciseNotFoundException(
				"No next exercise found for user ID " + userId + " in lesson ID " + lessonId
			));
	}
}
