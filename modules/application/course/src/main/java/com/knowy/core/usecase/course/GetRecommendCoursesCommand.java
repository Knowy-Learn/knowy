package com.knowy.core.usecase.course;

import com.knowy.core.domain.Pagination;

// JAVADOC
public record GetRecommendCoursesCommand(int userId, Pagination pagination) {
}
