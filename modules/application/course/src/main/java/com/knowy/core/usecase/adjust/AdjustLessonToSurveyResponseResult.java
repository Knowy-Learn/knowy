package com.knowy.core.usecase.adjust;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.domain.UserLesson;

import java.util.List;

public record AdjustLessonToSurveyResponseResult(
	int userId,
	int lessonId,
	int exerciseId,
	List<UserExercise> userExercises,
	double lessonProgress,
	UserLesson.ProgressStatus lessonStatus
) {
}
