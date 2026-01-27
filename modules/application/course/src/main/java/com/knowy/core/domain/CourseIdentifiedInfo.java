package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

public record CourseIdentifiedInfo(
	int id,
	String title,
	String description,
	String image,
	String author,
	LocalDateTime creationDate,
	Set<Category> categories
) implements CourseInfo<Category> {
}
