package com.knowy.core.usecase.course;

import com.knowy.core.domain.CourseStatus;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.domain.UserCourse;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.UserCourseRepository;

import java.util.Set;

/**
 * Use case responsible for retrieving all courses associated with a specific user. This class handles the logic of
 * fetching user-specific course data with support for pagination.
 */
public class GetAllUserCoursesByUserIdUseCase {

	private final UserCourseRepository userCourseRepository;

	/**
	 * Constructs a new GetAllUserCoursesByUserIdUseCase.
	 *
	 * @param userCourseRepository the repository port used to access user course data
	 */
	public GetAllUserCoursesByUserIdUseCase(UserCourseRepository userCourseRepository) {
		this.userCourseRepository = userCourseRepository;
	}

	/**
	 * Executes the use case to retrieve a paginated list of courses for a given user.
	 *
	 * @param userId     the unique identifier of the user whose courses are being retrieved
	 * @param pagination the pagination parameters (page number, size, etc.)
	 * @return a {@link PagedResult} containing a list of {@link UserCourse} objects and metadata
	 */
	public PagedResult<UserCourse> execute(int userId, Set<CourseStatus> courseStatusIds, Pagination pagination) throws KnowyDataAccessException {
		return userCourseRepository.findAllByUserId(userId, ensureNotEmpty(courseStatusIds), pagination);
	}

	private Set<CourseStatus> ensureNotEmpty(Set<CourseStatus> courseStatuses) {
		return (courseStatuses == null || courseStatuses.isEmpty())
			? Set.of(CourseStatus.IN_PROGRESS, CourseStatus.NOT_STARTED)
			: courseStatuses;
	}
}
