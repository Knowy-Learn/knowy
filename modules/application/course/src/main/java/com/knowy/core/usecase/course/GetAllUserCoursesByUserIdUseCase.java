package com.knowy.core.usecase.course;

import com.knowy.core.domain.*;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import com.knowy.core.port.UserCourseRepository;

import java.util.Optional;
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
		checkCategoryFilterOperator(pagination);
		return userCourseRepository.findAllByUserId(userId, ensureNotEmpty(courseStatusIds), pagination);
	}

	private void checkCategoryFilterOperator(Pagination pagination) {
		findCategoryFilter(pagination)
			.ifPresent(filter -> {
				validateOperator(filter);
				validateValue(filter);
			});
	}

	private Optional<Filter> findCategoryFilter(Pagination pagination) {
		return pagination.filters().stream()
			.filter(filter -> "category".equals(filter.value()))
			.findFirst();
	}

	private void validateOperator(Filter filter) {
		if (filter.operator() != Filter.Operator.IN) {
			throw new KnowyIllegalArgumentRuntimeException("Category filter must use 'IN' operator.");
		}
	}
	private void validateValue(Filter filter) {
		if (filter.value() instanceof Set<?>) {
			throw new KnowyIllegalArgumentRuntimeException("Category filter must be Set collection");
		}
	}

	private Set<CourseStatus> ensureNotEmpty(Set<CourseStatus> courseStatuses) {
		return (courseStatuses == null || courseStatuses.isEmpty())
			? Set.of(CourseStatus.IN_PROGRESS, CourseStatus.NOT_STARTED)
			: courseStatuses;
	}
}
