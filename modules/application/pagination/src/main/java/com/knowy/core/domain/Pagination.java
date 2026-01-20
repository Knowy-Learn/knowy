package com.knowy.core.domain;

import java.util.List;
import java.util.Optional;

/**
 * A container for all data retrieval modifiers, including pagination, sorting, and filtering.
 * <p>
 * This record aggregates {@link Page}, {@link Order}, and a list of {@link Filter} to provide a comprehensive
 * specification for database queries or service-level data processing.
 * </p>
 *
 * @param page    the pagination parameters (offset and limit).
 * @param order   the sorting configuration for the results.
 * @param filters a list of criteria to narrow down the dataset.
 */
public record Pagination(
	Page page,
	Optional<Order> order,
	List<Filter> filters
) {
}