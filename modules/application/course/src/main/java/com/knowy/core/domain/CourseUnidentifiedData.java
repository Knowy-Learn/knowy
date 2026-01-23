package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * Contract for course data that has not yet been assigned a unique identity.
 * <p>
 * This interface is primarily used during the creation process or for templates, enforcing the presence of all core
 * attributes without requiring an ID.
 */
public interface CourseUnidentifiedData extends CourseData<CategoryUnidentifiedData, LessonUnidentifiedData> {

	/**
	 * An immutable implementation of unidentified course data with built-in validation.
	 *
	 * @param title        The display title.
	 * @param description  The detailed summary.
	 * @param image        The cover image reference.
	 * @param author       The content creator.
	 * @param creationDate Initial publication timestamp.
	 * @param categories   The set of associated categories (unidentified).
	 * @param lessons      The set of associated lessons (unidentified).
	 */
	record InmutableCourseData(
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate,
		Set<CategoryUnidentifiedData> categories,
		Set<LessonUnidentifiedData> lessons
	) implements CourseUnidentifiedData {

		/**
		 * Validates that all course components are non-null upon instantiation.
		 *
		 * @throws NullPointerException if any required field is null.
		 */
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
