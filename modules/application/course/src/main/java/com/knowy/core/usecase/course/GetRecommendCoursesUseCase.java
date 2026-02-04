package com.knowy.core.usecase.course;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.exception.data.KnowyDataAccessException;
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
		return courseRepository.findAllRandomUnsubscribedUsers(command.userId(), command.pagination());
	}
}

