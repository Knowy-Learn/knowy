package com.knowy.core.domain;

import java.util.Collection;

/**
 * Represents the configuration for sorting a collection of elements. This record encapsulates the specific properties
 * to sort by, the strategy to apply (e.g., chronological or alphabetical), and the sort order.
 *
 * @param <T>        the type of the properties/fields being sorted
 * @param properties the collection of properties to be used as sort criteria; must not be null or empty
 * @param strategy   the logic applied to perform the sort (ALPHABETICAL, DATE, etc.)
 * @param direction  the order of the results; defaults to {@link SortDirection#ASCENDING} if null
 */
public record Order<T>(
	Collection<T> properties,
	SortStrategy strategy,
	SortDirection direction
) {

	/**
	 * Compact constructor to validate sort configuration.
	 * @throws IllegalArgumentException if properties is null or empty
	 */
	public Order {
		if (properties == null || properties.isEmpty()) {
			throw new IllegalArgumentException("Properties for sorting cannot be null or empty");
		}

		direction = (direction == null) ? SortDirection.ASCENDING : direction;
		strategy = (strategy == null) ? SortStrategy.ALPHABETICAL : strategy;
	}

	/**
	 * Defines the possible directions for the sorting operation.
	 */
	public enum SortDirection {
		ASCENDING,
		DESCENDING
	}

	/**
	 * Defines the criteria or algorithm type used to compare elements.
	 */
	public enum SortStrategy {
		ALPHABETICAL,
		PROGRESS,
		DATE
	}
}
