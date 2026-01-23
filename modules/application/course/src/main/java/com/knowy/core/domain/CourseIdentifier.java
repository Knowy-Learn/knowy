package com.knowy.core.domain;

/**
 * Identity Contract for the Course domain.
 * <p>
 * Defines the unique reference for a course, allowing other layers to interact with the entity using only its
 * identifier.
 */
public interface CourseIdentifier {
	int id();
}
