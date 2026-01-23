package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

public interface CourseMinData<C extends CategoryUnidentifiedData> {

	String title();

	String description();

	String image();

	String author();

	LocalDateTime creationDate();

	Set<C> categories();

}
