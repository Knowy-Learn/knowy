package com.knowy.core.domain;

import java.util.Collection;

/**
 * A container for a paginated subset of data along with its associated metadata.
 *
 * @param <T>        the type of elements contained in the collection
 * @param page       the pagination parameters used to fetch this result
 * @param collection the actual data subset for the current page
 */
public record PagedResult<T>(Page page, Collection<T> collection) {

	/**
	 * Returns the number of elements present in the current page.
	 *
	 * @return the size of the current collection
	 */
	public int count() {
		return collection.size();
	}

	/**
	 * Calculates the total number of pages based on the current collection size and the defined page size.
	 *
	 * @return the total number of pages (rounded up)
	 */
	public int pages() {
		return (int) Math.ceil((double) count() / page.size());
	}
}
