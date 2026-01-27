package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Composite contract for course information.
 * <p>
 * Combines both {@link CourseIdentifier} and {@link CourseMinData} to provide a complete identified summary of a
 * course.
 */
public interface CourseInfo<C extends CategoryUnidentifiedData> extends CourseIdentifier, CourseMinData<C> {

	/**
	 * Immutable implementation of course summary data.
	 * <p>
	 * Used as a data carrier for read-only operations where full course details (like lessons) are not required.
	 *
	 * @param id           Unique identifier.
	 * @param title        Course title.
	 * @param description  Brief summary.
	 * @param image        Cover image URL/path.
	 * @param author       Creator's name.
	 * @param creationDate Timestamp of publication.
	 * @param categories   Set of associated category metadata.
	 */
	record InmutableCourseInfo(
		int id,
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate,
		Set<CategoryUnidentifiedData> categories
	) implements CourseInfo<CategoryUnidentifiedData> {

		/**
		 * Transformation constructor.
		 *
		 * @param id            The unique identifier to assign.
		 * @param courseMinData The source metadata to wrap.
		 */
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
