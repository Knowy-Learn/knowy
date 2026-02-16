package com.knowy.core.usecase.course;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.UserLessonRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Retrieves enrolled courses for a user, including lesson status and calculated progress.
 */
public class FindCoursesWithProgressUseCase {

	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs a new {@code GetAllCoursesWithProgressUseCase} with the specified repository.
	 *
	 * @param userLessonRepository Repository to fetch user's lesson subscriptions.
	 */
	public FindCoursesWithProgressUseCase(UserLessonRepository userLessonRepository) {
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Executes the use case to retrieve all courses along with the user's progress for each.
	 *
	 * @param userId the ID of the user whose course progress is to be retrieved
	 * @return a list of {@link FindCoursesWithProgressResult}, each containing a course ID, the user's lessons, and
	 * progress
	 * @throws KnowyInconsistentDataException if there is an inconsistency while retrieving lessons or calculating
	 *                                        progress
	 */
	public List<FindCoursesWithProgressResult> execute(int userId) throws KnowyInconsistentDataException {
		List<UserLesson> userLessons = userLessonRepository.findAllWhereUserIsSubscribed(userId);

		Map<Integer, List<UserLesson>> lessonByCourses = userLessons.stream()
			.collect(Collectors.groupingBy(
				userLesson -> userLesson.lesson().courseId())
			);

		return lessonByCourses.entrySet().stream()
			.map(this::toCoursesWithProgress)
			.toList();
	}

	private FindCoursesWithProgressResult toCoursesWithProgress(Map.Entry<Integer, List<UserLesson>> entry) {
		return new FindCoursesWithProgressResult(
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
