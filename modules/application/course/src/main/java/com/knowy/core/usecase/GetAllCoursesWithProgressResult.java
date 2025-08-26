package com.knowy.core.usecase;

import com.knowy.core.domain.UserLesson;

import java.util.List;

public record GetAllCoursesWithProgressResult(int courseId, List<UserLesson> userLessons, float progress) {
}
