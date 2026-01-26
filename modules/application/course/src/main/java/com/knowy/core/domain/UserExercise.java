package com.knowy.core.domain;

import java.time.LocalDateTime;

/**
 * Domain record representing the progress of a user in a specific exercise.
 * <p>
 * This aggregate maintains the relationship between the learner and the content, tracking performance metrics and
 * scheduling future reviews.
 *
 * @param userId     The unique identifier of the user.
 * @param exercise   The specific exercise being tracked.
 * @param rate       The performance score, normalized between 0 and 100.
 * @param nextReview The timestamp for the next scheduled review session.
 */
public record UserExercise(int userId, Exercise exercise, Integer rate, LocalDateTime nextReview) {

	/**
	 * Canonical constructor with rate normalization.
	 * <p>
	 * Ensures that the performance rate always falls within the valid business range (0-100%) regardless of the input
	 * value.
	 */
	public UserExercise(int userId, Exercise exercise, Integer rate, LocalDateTime nextReview) {
		this.userId = userId;
		this.exercise = exercise;
		this.rate = normalizeRate(rate);
		this.nextReview = nextReview;
	}

	private static Integer normalizeRate(Integer rate) {
		return Math.clamp(rate, 0, 100);
	}
}