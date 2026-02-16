package com.knowy.core.usecase.course;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.Filter;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.util.KnowyUseCase;

import java.util.Optional;

/**
 * Use case for retrieving a randomized, paginated list of course recommendations for a specific user, excluding courses
 * the user is already enrolled in.
 * <p>
 * It enforces business rules regarding filtering, specifically ensuring that category-based filters use the correct
 * logical operators.
 */
public class GetRecommendCoursesUseCase implements KnowyUseCase<GetRecommendCoursesCommand, PagedResult<Course>> {

	private final CourseRepository courseRepository;

	/**
	 * Constructs the use case with the necessary repository.
	 *
	 * @param courseRepository the repository used to fetch course data.
	 */
	public GetRecommendCoursesUseCase(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	/**
	 * Executes the recommendation logic.
	 * <p>
	 * First, it validates that the category filters in the pagination criteria are well-formed. Then, it queries the
	 * repository for random courses that the user has not yet subscribed to.
	 *
	 * @param command the command object containing the user ID and pagination settings.
	 * @return a {@link PagedResult} containing the recommended {@link Course} entities.
	 * @throws KnowyDataAccessException             if an error occurs during data retrieval.
	 * @throws KnowyIllegalArgumentRuntimeException if the provided filters are invalid.
	 */
	@Override
	public PagedResult<Course> execute(GetRecommendCoursesCommand command) throws KnowyDataAccessException {
		checkCategoryFilterOperator(command.pagination());
		assertOrderIsEmpty(command.pagination());

		return courseRepository.findRandomNotSubscribedByUserId(command.userId(), command.pagination());
	}

	private void checkCategoryFilterOperator(Pagination pagination) {
		findCategoryFilter(pagination)
			.ifPresent(this::validateOperator);
	}

	private Optional<Filter> findCategoryFilter(Pagination pagination) {
		return pagination.filters().stream()
			.filter(f -> "category".equals(f.value()))
			.findFirst();
	}

	private void validateOperator(Filter filter) {
		if (filter.operator() != Filter.Operator.IN) {
			throw new KnowyIllegalArgumentRuntimeException("Category filter must use 'IN' operator.");
		}
	}

	private void assertOrderIsEmpty(Pagination pagination) {
		if (pagination.order().isPresent()) {
			throw new KnowyIllegalArgumentRuntimeException("Order must be empty.");
		}
	}
}

