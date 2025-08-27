package com.knowy.core.usecase.exercise;

import com.knowy.core.domain.Exercise;
import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.port.ExerciseRepository;
import com.knowy.core.port.UserExerciseRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Use case for retrieving a user's exercise by ID, or creating a new {@link UserExercise} if it does not exist.
 * <p>
 * This use case first attempts to fetch an existing {@link UserExercise} for the given user and exercise ID. If none is
 * found, a new {@link UserExercise} is created with default progress and the current timestamp.
 */
public class GetUserExerciseByIdOrCreate {

	private final UserExerciseRepository userExerciseRepository;
	private final ExerciseRepository exerciseRepository;

	/**
	 * Constructs a new {@code GetUserExerciseByIdOrCreate} with the specified repositories.
	 *
	 * @param userExerciseRepository the repository for accessing user exercises
	 * @param exerciseRepository     the repository for accessing exercises
	 */
	public GetUserExerciseByIdOrCreate(UserExerciseRepository userExerciseRepository, ExerciseRepository exerciseRepository) {
		this.userExerciseRepository = userExerciseRepository;
		this.exerciseRepository = exerciseRepository;
	}

	/**
	 * Retrieves the {@link UserExercise} for the specified user and exercise ID, creating a new one if none exists.
	 *
	 * @param userId     the ID of the user
	 * @param exerciseId the ID of the exercise
	 * @return the existing or newly created {@link UserExercise}
	 * @throws KnowyDataAccessException       if an error occurs while accessing the repository
	 * @throws KnowyExerciseNotFoundException if the specified exercise does not exist
	 */
	public UserExercise execute(int userId, int exerciseId) throws KnowyDataAccessException {
		Optional<UserExercise> userExercise = userExerciseRepository.findById(userId, exerciseId);
		if (userExercise.isEmpty()) {
			return new UserExercise(userId, getExerciseOrThrow(exerciseId), 0, LocalDateTime.now());
		}
		return userExercise.get();
	}

	private Exercise getExerciseOrThrow(int exerciseId) throws KnowyExerciseNotFoundException {
		return exerciseRepository.findById(exerciseId)
			.orElseThrow(() -> new KnowyExerciseNotFoundException("Exercise " + exerciseId + " not found"));
	}
}
