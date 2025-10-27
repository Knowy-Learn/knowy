package com.knowy.server.infrastructure.controller.dto;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.UserExercise;

import java.util.List;

public record ExerciseDto(
	int lessonId,
	int exerciseId,
	String courseName,
	int correctPercentage,
	String questionText,
	String imgPath,
	List<ExerciseOptionDto> options
) {

	public static ExerciseDto fromDomain(UserExercise userExercise, Course course) {
		return new ExerciseDto(
			userExercise.exercise().lessonId(),
			userExercise.exercise().id(),
			course.title(),
			userExercise.rate(),
			userExercise.exercise().statement(),
			"TODO",
			userExercise.exercise().options()
				.stream()
				.map(ExerciseOptionDto::fromDomain)
				.toList()
		);
	}

	public static ExerciseDto fromDomain(UserExercise userExercise, Course course, int answerId) {
		List<ExerciseOptionDto> options = userExercise.exercise().options()
			.stream()
			.map(option -> ExerciseOptionDto.fromDomain(option, answerId))
			.toList();

		return new ExerciseDto(
			userExercise.exercise().lessonId(),
			userExercise.exercise().id(),
			course.title(),
			userExercise.rate(),
			userExercise.exercise().statement(),
			"TODO",
			options
		);
	}
}
