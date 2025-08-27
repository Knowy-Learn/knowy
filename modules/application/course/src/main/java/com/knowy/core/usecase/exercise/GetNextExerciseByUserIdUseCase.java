package com.knowy.core.usecase.exercise;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.port.UserExerciseRepository;

/**
 * Use case for retrieving the next exercise assigned to a specific user, regardless of the lesson or course.
 * <p>
 * This use case queries the {@link UserExerciseRepository} to find the next {@link UserExercise} pending for the given
 * user. If no exercise is available, a {@link KnowyExerciseNotFoundException} is thrown.
 */
public class GetNextExerciseByUserIdUseCase {

	private final UserExerciseRepository userExerciseRepository;


	/**
	 * Constructs a new {@code GetNextExerciseByUserIdUseCase} with the specified repository.
	 *
	 * @param userExerciseRepository the repository used to access user exercises
	 */
	public GetNextExerciseByUserIdUseCase(UserExerciseRepository userExerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
	}

	/**
	 * Retrieves the next exercise for the specified user.
	 *
	 * @param userId the ID of the user whose next exercise should be retrieved
	 * @return the next {@link UserExercise} assigned to the user
	 * @throws KnowyDataAccessException       if an error occurs while accessing the repository
	 * @throws KnowyExerciseNotFoundException if no next exercise exists for the given user
	 */
	public UserExercise execute(int userId) throws KnowyDataAccessException {
		return userExerciseRepository.findNextExerciseByUserId(userId)
			.orElseThrow(() -> new KnowyExerciseNotFoundException("No next exercise found for user ID " + userId));
	}
}
