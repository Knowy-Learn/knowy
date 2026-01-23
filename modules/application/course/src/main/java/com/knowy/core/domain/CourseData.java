package com.knowy.core.domain;

import java.util.Set;

public interface CourseData<C extends CategoryUnidentifiedData, L extends LessonData<? extends DocumentationData, ? extends ExerciseData<? extends OptionData>>>
	extends CourseMinData<C> {

	Set<L> lessons();
}
