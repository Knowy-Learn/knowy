package com.knowy.core.port;

import com.knowy.core.domain.ProgressStatus;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.domain.UserCourse;
import com.knowy.core.exception.data.KnowyDataAccessException;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface defining the contract for managing and retrieving {@link UserCourse} data. This port should be
 * implemented by infrastructure adapters to interact with the database.
 */
public interface UserCourseRepository {

	/**
	 * Retrieves a user's progress within a specific course.
	 *
	 * @param userId the unique identifier of the user
	 * @param courseId the unique identifier of the course
	 * @return an Optional containing the UserCourse, or empty if no records exist
	 * @throws KnowyDataAccessException if a data access error occurs
	 */
	Optional<UserCourse> findById(int userId, int courseId) throws KnowyDataAccessException;

	/**
	 * Retrieves a paginated list of all courses associated with a specific user.
	 *
	 * @param userId     the unique identifier of the user
	 * @param pagination the pagination configuration
	 * @return a {@link PagedResult} containing the list of {@link UserCourse} records
	 * @throws KnowyDataAccessException if there is an error accessing to the data or processing the paginated request
	 */
	PagedResult<UserCourse> findAllByUserId(int userId, Set<ProgressStatus> progressStatusIds, Pagination pagination) throws KnowyDataAccessException;
}
