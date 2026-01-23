package com.knowy.core.domain;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Contract for a course's summary data.
 * <p>
 * This interface focuses on the descriptive metadata required for high-level listings, isolating it from the heavy
 * content like lesson structures.
 *
 * @param <C> the specific type of category data, extending the unidentified contract
 */
public interface CourseMinData<C extends CategoryUnidentifiedData> {

	String title();

	String description();

	String image();

	String author();

	LocalDateTime creationDate();

	Set<C> categories();

}
