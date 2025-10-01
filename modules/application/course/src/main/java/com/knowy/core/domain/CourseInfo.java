package com.knowy.core.domain;

import java.time.LocalDateTime;

public record CourseInfo(
	int id,
	String title,
	String description,
	String image,
	String author,
	LocalDateTime creationDate
) implements CourseIdentifier, CourseMinData {

	public CourseInfo(int id, CourseMinData courseMinData) {
		this(
			id,
			courseMinData.title(),
			courseMinData.description(),
			courseMinData.image(),
			courseMinData.author(),
			courseMinData.creationDate()
		);
	}
}
