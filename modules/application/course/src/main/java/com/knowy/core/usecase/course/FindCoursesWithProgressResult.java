package com.knowy.core.usecase.course;

import com.knowy.core.domain.UserLesson;

import java.util.List;

public record FindCoursesWithProgressResult(int courseId, List<UserLesson> userLessons, float progress) {
}
