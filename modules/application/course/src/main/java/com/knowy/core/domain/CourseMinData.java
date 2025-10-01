package com.knowy.core.domain;

import java.time.LocalDateTime;

public interface CourseMinData {

	String title();

	String description();

	String image();

	String author();

	LocalDateTime creationDate();

}
