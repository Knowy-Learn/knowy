package com.knowy.core.usecase.lesson;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserLessonRepository;

import java.util.List;

/**
 * Use case for retrieving all {@link UserLesson} entries for a specific user and course.
 */
public class GetAllUserLessonByCourseIdUseCase {

	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs a new {@code GetAllUserLessonByCourseIdUseCase} with the specified repository.
	 *
	 * @param userLessonRepository the repository used to access user lessons
	 */
	public GetAllUserLessonByCourseIdUseCase(UserLessonRepository userLessonRepository) {
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Retrieves all lessons for a given user and course.
	 *
	 * @param userId   the ID of the user
	 * @param courseId the ID of the course
	 * @return a {@link List} of {@link UserLesson} associated with the user in the course
	 * @throws KnowyInconsistentDataException if there is an issue accessing the data
	 */
	public List<UserLesson> execute(int userId, int courseId) throws KnowyInconsistentDataException {
		return userLessonRepository.findAllByUserIdAndCourseId(userId, courseId);
	}
}
