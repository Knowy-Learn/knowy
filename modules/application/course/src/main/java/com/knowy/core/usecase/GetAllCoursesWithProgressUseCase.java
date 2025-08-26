package com.knowy.core.usecase;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserLessonRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Use case for retrieving all courses a user is subscribed to, along with their progress in each course.
 * <p>
 * This use case fetches all lessons for a specific user, groups them by course, calculates the user's progress in each
 * course based on lesson completion status, and returns a list of results containing the course ID, the user's lessons,
 * and the computed progress.
 */
public class GetAllCoursesWithProgressUseCase {

	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs a new {@code GetAllCoursesWithProgressUseCase} with the specified repository.
	 *
	 * @param userLessonRepository Repository to fetch user's lesson subscriptions.
	 */
	public GetAllCoursesWithProgressUseCase(UserLessonRepository userLessonRepository) {
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Executes the use case to retrieve all courses along with the user's progress for each.
	 *
	 * @param userId the ID of the user whose course progress is to be retrieved
	 * @return a list of {@link GetAllCoursesWithProgressResult}, each containing a course ID, the user's lessons, and
	 * progress
	 * @throws KnowyInconsistentDataException if there is an inconsistency while retrieving lessons or calculating
	 *                                        progress
	 */
	public List<GetAllCoursesWithProgressResult> execute(int userId) throws KnowyInconsistentDataException {
		List<UserLesson> userLessons = userLessonRepository.findAllWhereUserIsSubscribed(userId);

		Map<Integer, List<UserLesson>> lessonByCourses = userLessons.stream()
			.collect(Collectors.groupingBy(
				userLesson -> userLesson.lesson().courseId())
			);

		return lessonByCourses.entrySet().stream()
			.map(this::toCoursesWithProgress)
			.toList();
	}

	private GetAllCoursesWithProgressResult toCoursesWithProgress(Map.Entry<Integer, List<UserLesson>> entry) {
		return new GetAllCoursesWithProgressResult(
			entry.getKey(),
			entry.getValue(),
			calculateCourseProgress(entry.getValue())
		);
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
}
