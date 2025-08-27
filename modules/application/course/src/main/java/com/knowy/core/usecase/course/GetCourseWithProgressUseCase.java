package com.knowy.core.usecase.course;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.UserLessonRepository;

import java.util.List;

/**
 * Use case for retrieving a course along with a user's progress in that course.
 */
public class GetCourseWithProgressUseCase {

	private final CourseRepository courseRepository;
	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs a new {@code GetCourseWithProgressUseCase}.
	 *
	 * @param courseRepository     repository to fetch course data
	 * @param userLessonRepository repository to fetch user's lesson data
	 */
	public GetCourseWithProgressUseCase(CourseRepository courseRepository, UserLessonRepository userLessonRepository) {
		this.courseRepository = courseRepository;
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Executes the use case to retrieve a course along with the user's progress.
	 *
	 * @param userId   the ID of the user
	 * @param courseId the ID of the course
	 * @return a {@link GetCourseWithProgressResult} containing the course and the user's progress
	 * @throws KnowyInconsistentDataException if no lessons are found for the user in the given course
	 * @throws KnowyCourseNotFound            if the course with the given ID does not exist
	 */
	public GetCourseWithProgressResult execute(int userId, int courseId) throws KnowyInconsistentDataException {
		List<UserLesson> userLessons = getAllUserLessonByUserIdAndCourseId(userId, courseId);
		float totalCourseProgress = calculateCourseProgress(userLessons);

		Course course = getCourseByCourseId(courseId);

		return new GetCourseWithProgressResult(course, totalCourseProgress);
	}

	private List<UserLesson> getAllUserLessonByUserIdAndCourseId(int userId, int courseId) throws KnowyInconsistentDataException {
		List<UserLesson> userLessons = userLessonRepository.findAllByUserIdAndCourseId(userId, courseId);
		if (userLessons.isEmpty()) {
			throw new KnowyInconsistentDataException(
				"No lessons found for userId " + userId + " in courseId " + courseId
			);
		}

		return userLessons;
	}

	private float calculateCourseProgress(List<UserLesson> userLessons) {
		return (float) userLessons.stream()
			.mapToDouble(this::lessonProgressValue)
			.average()
			.orElse(0.0);
	}

	private double lessonProgressValue(UserLesson userLesson) {
		return switch (userLesson.status()) {
			case PENDING -> 0.0;
			case IN_PROGRESS -> 0.5;
			case COMPLETED -> 1.0;
		};
	}

	private Course getCourseByCourseId(int courseId) throws KnowyInconsistentDataException {
		return courseRepository.findById(courseId)
			.orElseThrow(() -> new KnowyCourseNotFound("Course with id " + courseId + " not found"));
	}
}
