package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
	public Course(
		int id,
		String title,
		String description,
		String image,
		String author,
		LocalDateTime creationDate
	) {
		this(id, title, description, image, author, creationDate, new HashSet<>(), new HashSet<>());
	}
}
