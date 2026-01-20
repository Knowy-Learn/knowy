package com.knowy.core.domain;

import java.util.Objects;

/**
 * Represents the configuration for sorting a collection of elements. This record encapsulates the specific field to
 * sort by and the sort order.
 *
 * @param field     the name of the field or property to sort by.
 * @param direction the order of the results; defaults to {@link SortDirection#ASCENDING} if null.
 */
public record Order(
	String field,
	SortDirection direction
) {

	/**
	 * Compact constructor to validate sort configuration. Ensures that the direction is never null by defaulting to
	 * {@link SortDirection#ASCENDING}.
	 */
	public Order {
		direction = Objects.requireNonNullElse(direction, SortDirection.ASCENDING);
	}

	/**
	 * Defines the possible directions for the sorting operation.
	 */
	public enum SortDirection {
		ASCENDING,
		DESCENDING
	}
}