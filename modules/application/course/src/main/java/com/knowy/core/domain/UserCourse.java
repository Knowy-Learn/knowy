package com.knowy.core.domain;

import java.util.List;

/**
 * Represents the progress and relationship between a user and a specific course.
 *
 * @param userId      the unique identifier of the user
 * @param courseInfo  general metadata and details about the course
 * @param userLessons the list of individual lesson progress records for this user
 */
public record UserCourse(int userId, CourseInfo<Category> courseInfo, List<UserLesson> userLessons) {

	/**
	 * Calculates the overall progress of the course as a normalized value between 0.0 and 1.0.
	 * <p>
	 * The progress is determined by the average weight of all lesson statuses:
	 * <ul>
	 * <li><b>COMPLETED:</b> 1.0 (100%)</li>
	 * <li><b>PENDING:</b> 0.5 (50%)</li>
	 * <li><b>Other/NOT_STARTED:</b> 0.0 (0%)</li>
	 * </ul>
	 *
	 * @return a double representing the total progress (e.g., 0.75 for 75%)
	 */
	public double courseProgress() {
		if (userLessons.isEmpty()) {
			return 0.0;
		}

		return userLessons.stream()
			.map(UserLesson::status)
			.mapToDouble(this::statusToWeight)
			.average()
			.orElse(0.0);
	}

	private double statusToWeight(ProgressStatus status) {
		return switch (status) {
			case COMPLETED -> 1.0;
			case IN_PROGRESS -> 0.5;
			default -> 0.0;
		};
	}
}
