package com.knowy.core.port;

import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.domain.UserCourse;
import com.knowy.core.exception.data.KnowyDataAccessException;

import java.util.List;

/**
 * Repository interface defining the contract for managing and retrieving {@link UserCourse} data. This port should be
 * implemented by infrastructure adapters to interact with the database.
 */
public interface UserCourseRepository {

	/**
	 * Retrieves a specific course enrollment record for a user.
	 *
	 * @param userId   the unique identifier of the user
	 * @param courseId the unique identifier of the course
	 * @return the {@link UserCourse} record associated with the user and course
	 * @throws KnowyDataAccessException if there is an error accessing to the data
	 */
	UserCourse findById(int userId, int courseId) throws KnowyDataAccessException;

	/**
	 * Retrieves a paginated list of all courses associated with a specific user.
	 *
	 * @param userId     the unique identifier of the user
	 * @param pagination the pagination configuration
	 * @return a {@link PagedResult} containing the list of {@link UserCourse} records
	 * @throws KnowyDataAccessException if there is an error accessing to the data or processing the paginated request
	 */
	PagedResult<List<UserCourse>> findAllByUserId(int userId, Pagination pagination) throws KnowyDataAccessException;
}
