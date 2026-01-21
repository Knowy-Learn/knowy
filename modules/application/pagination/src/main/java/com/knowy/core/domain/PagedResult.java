package com.knowy.core.domain;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A container for a paginated subset of data along with its associated metadata.
 *
 * @param <T>        the type of elements contained in the collection
 * @param page       the pagination parameters used to fetch this result
 * @param collection the actual data subset for the current page
 */
public record PagedResult<T>(Page page, Collection<T> collection, long totalItems) {

	/**
	 * Constructs a new {@code PagedResult}.
	 *
	 * @param collection the collection of elements; if {@code null}, defaults to an empty immutable list.
	 */
	public PagedResult {
		Objects.requireNonNull(page, "page parameters must not be null");
		collection = Objects.requireNonNullElse(collection, List.of());
	}

	/**
	 * Calculates the total number of pages based on the current collection size and the defined page size.
	 *
	 * @return the total number of pages (rounded up)
	 */
	public int pages() {
		return (int) Math.ceil((double) totalItems() / page.size());
	}
}
