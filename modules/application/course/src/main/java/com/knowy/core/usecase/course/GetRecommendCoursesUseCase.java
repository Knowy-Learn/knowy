package com.knowy.core.usecase.course;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.Filter;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.util.KnowyUseCase;

// JAVADOC
public class GetRecommendCoursesUseCase implements KnowyUseCase<GetRecommendCoursesCommand, PagedResult<Course>> {

	private final CourseRepository courseRepository;

	public GetRecommendCoursesUseCase(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}


	@Override
	public PagedResult<Course> execute(GetRecommendCoursesCommand command) throws KnowyDataAccessException {
		checkCategoryFilterOperator(command.pagination());

		return courseRepository.findAllRandomUnsubscribedUsers(command.userId(), command.pagination());
	}

	private void checkCategoryFilterOperator(Pagination pagination) {
		pagination.filters().stream()
			.filter(f -> "category".equals(f.value()))
			.reduce((first, second) -> {
				// TODO: Tenes que hacer que la lista este ordenada pero sin filtros repetibles.
				throw new KnowyIllegalArgumentRuntimeException("Multiple 'category' filters are not allowed.");
			})
			.ifPresent(filter -> {
				if (filter.operator() != Filter.Operator.IN) {
					throw new KnowyIllegalArgumentRuntimeException("Category filter must use 'IN' operator.");
				}
			});
	}
}

