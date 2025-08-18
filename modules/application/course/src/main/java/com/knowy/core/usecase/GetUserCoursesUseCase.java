package com.knowy.core.usecase;

import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.UserLessonRepository;

import java.util.List;

/**
 * Use case for retrieving all courses associated with a given user.
 */
public class GetUserCoursesUseCase {

	private final UserLessonRepository userLessonRepository;
	private final CourseRepository courseRepository;

	/**
	 * Creates a new {@code GetUserCoursesUseCase}.
	 *
	 * @param userLessonRepository Repository for retrieving course IDs linked to users.
	 * @param courseRepository     Repository for retrieving course details by ID.
	 */
	public GetUserCoursesUseCase(UserLessonRepository userLessonRepository, CourseRepository courseRepository) {
		this.userLessonRepository = userLessonRepository;
		this.courseRepository = courseRepository;
	}

	/**
	 * Executes the use case: retrieves all courses that the specified user is enrolled in.
	 *
	 * @param userId the ID of the user whose courses should be retrieved.
	 * @return a list of {@link Course} entities. Returns an empty list if the user has no courses.
	 * @throws KnowyInconsistentDataException if inconsistencies occur while fetching course data.
	 */
	public List<Course> execute(Integer userId) throws KnowyInconsistentDataException {
		List<Integer> courseIds = userLessonRepository.findCourseIdsByUserId(userId);
		if (courseIds.isEmpty()) {
			return List.of();
		}
		return courseRepository.findAllById(courseIds);
	}
}
