package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public interface CourseUnidentifiedData extends CourseData<CategoryUnidentifiedData, LessonUnidentifiedData> {

	record InmutableCourseData(
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate,
		Set<CategoryUnidentifiedData> categories,
		Set<LessonUnidentifiedData> lessons
	) implements CourseUnidentifiedData {
		public InmutableCourseData(
			String title,
			String description,
			String image,
			String author,
			LocalDateTime creationDate,
			Set<CategoryUnidentifiedData> categories,
			Set<LessonUnidentifiedData> lessons
		) {
			this.title = Objects.requireNonNull(title, "title cannot be null");
			this.description = Objects.requireNonNull(description, "description cannot be null");
			this.image = Objects.requireNonNull(image, "image cannot be null");
			this.author = Objects.requireNonNull(author, "author cannot be null");
			this.creationDate = Objects.requireNonNull(creationDate, "creation date cannot be null");
			this.categories = Objects.requireNonNull(categories, "categories cannot be null");
			this.lessons = Objects.requireNonNull(lessons, "lessons cannot be null");
		}
	}
}
