package com.knowy.core.domain;

import java.time.LocalDateTime;

public record UserExercise(int userId, Exercise exercise, Integer rate, LocalDateTime nextReview) {

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