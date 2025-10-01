package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

public interface CourseData extends CourseMinData {

	Set<Category> categories();

	Set<Lesson> lessons();

	record InmutableLessonData(
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate,
		Set<Category> categories,
		Set<Lesson> lessons
	) implements CourseData {
	}
}
