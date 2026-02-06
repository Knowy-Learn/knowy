package com.knowy.core.domain;

import java.util.Optional;
import java.util.Set;

/**
 * A container for all data retrieval modifiers, including pagination, sorting, and filtering.
 * <p>
 * This record aggregates {@link Page}, {@link Order}, and a set of {@link Filter} to provide a comprehensive
 * specification for database queries or service-level data processing.
 * </p>
 *
 * @param page    the pagination parameters (offset and limit).
 * @param order   the sorting configuration for the results.
 * @param filters a set of criteria to narrow down the dataset.
 */
public record Pagination(
	Page page,
	Optional<Order> order,
	Set<Filter> filters
) {
}