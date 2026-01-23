package com.knowy.core.domain;

import java.util.Set;

/**
 * Structural Generic Contract.
 * <p>
 * This is the high-level blueprint that defines the relationship between a Course, its Categories, and its deeply
 * nested Lessons. It serves as the base for any "Complete Course" view regardless of its identification state.
 *
 * @param <C> The category view (constrained to Unidentified state).
 * @param <L> The lesson view (enforces a full tree of Documentation and Exercises).
 */
public interface CourseData<C extends CategoryUnidentifiedData, L extends LessonData<? extends DocumentationUnidentifiedData, ? extends ExerciseData<? extends OptionUnidentifiedData>>>
	extends CourseMinData<C> {

	Set<L> lessons();
}
