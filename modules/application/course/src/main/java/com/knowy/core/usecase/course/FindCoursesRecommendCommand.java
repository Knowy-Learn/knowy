package com.knowy.core.usecase.course;

import com.knowy.core.domain.Pagination;

/**
 * Command object that encapsulates the necessary data to request course recommendations.
 * <p>
 * This record serves as the input parameter for the {@code GetRecommendCoursesUseCase}, ensuring that both the user
 * identity and the structural requirements for pagination and filtering are passed together.
 *
 * @param userId     the unique identifier of the user for whom recommendations are being generated.
 * @param pagination the {@link Pagination} criteria, including page limits, sorting, and filters.
 */
public record FindCoursesRecommendCommand(int userId, Pagination pagination) {
}
