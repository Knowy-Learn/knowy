package com.knowy.core.usecase;

import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;

import java.util.List;

/**
 * Use case for retrieving all available courses in a randomized order.
 */
public class GetAllCoursesRandomized {

	private final CourseRepository courseRepository;

	/**
	 * Constructs the use case with the required repository dependency.
	 *
	 * @param courseRepository the repository used to access course data
	 */
	public GetAllCoursesRandomized(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	/**
	 * Retrieves all courses in a randomized order.
	 *
	 * @return a randomized list of {@link Course} entities
	 * @throws KnowyInconsistentDataException if inconsistencies occur when fetching course data
	 */
	public List<Course> execute() throws KnowyInconsistentDataException {
		return courseRepository.findAllRandom();
	}
}
