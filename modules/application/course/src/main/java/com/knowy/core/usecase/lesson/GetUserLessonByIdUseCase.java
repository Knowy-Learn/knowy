package com.knowy.core.usecase.lesson;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyUserLessonNotFoundException;
import com.knowy.core.port.UserLessonRepository;

/**
 * Use case for retrieving a specific {@link UserLesson} by user ID and lesson ID.
 */
public class GetUserLessonByIdUseCase {

	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs a new {@code GetUserLessonByIdUseCase} with the provided repository.
	 *
	 * @param userLessonRepository the repository used to access user lessons
	 */
	public GetUserLessonByIdUseCase(UserLessonRepository userLessonRepository) {
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Retrieves the {@link UserLesson} associated with the given user ID and lesson ID.
	 *
	 * @param userId   the ID of the user
	 * @param lessonId the ID of the lesson
	 * @return the {@link UserLesson} if found
	 * @throws KnowyUserLessonNotFoundException if no lesson is found for the given user and lesson IDs
	 * @throws KnowyInconsistentDataException   if inconsistent data is encountered during retrieval
	 */
	public UserLesson execute(int userId, int lessonId) throws KnowyInconsistentDataException {
		return userLessonRepository.findById(userId, lessonId)
			.orElseThrow(() -> new KnowyUserLessonNotFoundException(
				"UserLesson not found: lessonId=" + lessonId + " userId=" + userId
			));
	}
}
