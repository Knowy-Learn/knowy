package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

public interface CourseData<C extends CategoryData, L extends LessonData<DocumentationData, ExerciseData<OptionData>>> extends CourseMinData {

	Set<C> categories();

	Set<L> lessons();

	record InmutableCourseData(
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate,
		Set<CategoryData> categories,
		Set<LessonData<DocumentationData, ExerciseData<OptionData>>> lessons
	) implements CourseData<CategoryData, LessonData<DocumentationData, ExerciseData<OptionData>>> {
	}
}
