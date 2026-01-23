package com.knowy.core.domain;

import java.util.Set;

public interface CourseData<C extends CategoryUnidentifiedData, L extends LessonData<? extends DocumentationUnidentifiedData, ? extends ExerciseData<? extends OptionUnidentifiedData>>>
	extends CourseMinData<C> {

	Set<L> lessons();
}
