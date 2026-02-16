package com.knowy.core.usecase.course;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.Filter;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import com.knowy.core.port.CourseRepository;

import java.util.Optional;

public class FindNotSubscribedUseCase {

	private final CourseRepository courseRepository;

	public FindNotSubscribedUseCase(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	public PagedResult<Course> execute(int userId, Pagination pagination) throws KnowyDataAccessException {
		checkCategoryFilterOperator(pagination);
		return courseRepository.findNotSubscribedByUserId(userId, pagination);
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
}
