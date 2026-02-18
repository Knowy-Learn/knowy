package com.knowy.core.usecase.course;

import com.knowy.core.domain.UserCourse;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.UserCourseRepository;

import java.util.Optional;

/**
 * Use case to retrieve a specific {@link UserCourse} by its unique identifiers.
 * <p>
 * This class serves as a business logic entry point, delegating the persistence lookup to the
 * {@link UserCourseRepository}.
 * </p>
 */
public class FindUserCourseByIdUseCase {

	private final UserCourseRepository userCourseRepository;

	/**
	 * Constructs the use case with its required repository dependency.
	 *
	 * @param userCourseRepository The repository for accessing user course data.
	 */
	public FindUserCourseByIdUseCase(UserCourseRepository userCourseRepository) {
		this.userCourseRepository = userCourseRepository;
	}

	/**
	 * Executes the search for a user course.
	 *
	 * @param userId   The unique ID of the user.
	 * @param courseId The unique ID of the course.
	 * @return An {@link Optional} with the found {@link UserCourse}, or empty if not present.
	 * @throws KnowyDataAccessException If an error occurs during repository access.
	 */
	public Optional<UserCourse> execute(int userId, int courseId) throws KnowyDataAccessException {
		return userCourseRepository.findById(userId, courseId);
	}
}
