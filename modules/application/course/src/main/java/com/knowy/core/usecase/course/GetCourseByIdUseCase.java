package com.knowy.core.usecase.course;

import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;

/**
 * Use case for retrieving a course by its identifier.
 */
public class GetCourseByIdUseCase {

	private final CourseRepository courseRepository;

	/**
	 * Constructs a new {@code GetCourseByIdUseCase}.
	 *
	 * @param courseRepository the repository used to access course data
	 */
	public GetCourseByIdUseCase(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}


	/**
	 * Retrieves a course by its unique identifier.
	 *
	 * @param courseId the ID of the course to retrieve
	 * @return the {@link Course} with the given ID
	 * @throws KnowyInconsistentDataException if no course with the given ID exists
	 */
	public Course execute(int courseId) throws KnowyInconsistentDataException {
		return courseRepository.findById(courseId)
			.orElseThrow(() -> new KnowyCourseNotFound("Course not found with ID: " + courseId));
	}
}
