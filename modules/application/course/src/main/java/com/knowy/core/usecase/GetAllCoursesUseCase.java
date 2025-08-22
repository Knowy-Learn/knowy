package com.knowy.core.usecase;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.port.CourseRepository;

import java.util.List;

/**
 * Use case for retrieving all courses with pagination support.
 */
public class GetAllCoursesUseCase {

	private final CourseRepository courseRepository;

	/**
	 * Constructs a new GetAllCoursesUseCase with the given course repository.
	 *
	 * @param courseRepository the repository used to access course data
	 */
	public GetAllCoursesUseCase(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	/**
	 * Executes the use case to retrieve all courses according to the given pagination.
	 *
	 * @param pagination the pagination parameters
	 * @return a list of courses
	 * @throws KnowyCourseNotFound if no courses are found
	 */
	public List<Course> execute(Pagination pagination) throws KnowyCourseNotFound {
		return courseRepository.findAll(pagination);
	}
}