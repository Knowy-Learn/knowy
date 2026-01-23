package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The Domain Aggregate Root. Merges identity and data state into a single source of truth.
 *
 * @param id           Unique identifier.
 * @param title        Course title.
 * @param description  Detailed summary.
 * @param image        URL or path to the cover image.
 * @param author       Name of the course creator.
 * @param creationDate Timestamp of entity creation.
 * @param categories   Set of associated tags/categories.
 * @param lessons      Set of content lessons.
 */
public record Course(
	int id,
	String title,
	String description,
	String image,
	String author,
	LocalDateTime creationDate,
	Set<Category> categories,
	Set<Lesson> lessons
) implements CourseIdentifier, CourseData<Category, Lesson> {

	/**
	 * Minimal constructor for initialization without collections.
	 */
	public Course(
		int id,
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate
	) {
		this(id, title, description, image, author, creationDate, new HashSet<>(), new LinkedHashSet<>());
	}
}
