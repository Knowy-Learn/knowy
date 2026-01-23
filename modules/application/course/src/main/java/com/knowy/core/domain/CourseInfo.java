package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

public interface CourseInfo extends CourseIdentifier, CourseMinData<CategoryUnidentifiedData> {

	record InmutableCourseInfo(
		int id,
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate,
		Set<CategoryUnidentifiedData> categories
	) implements CourseIdentifier, CourseMinData<CategoryUnidentifiedData> {

		public InmutableCourseInfo(int id, CourseMinData<CategoryUnidentifiedData> courseMinData) {
			this(
				id,
				courseMinData.title(),
				courseMinData.description(),
				courseMinData.image(),
				courseMinData.author(),
				courseMinData.creationDate(),
				courseMinData.categories()
			);
		}
	}
}
