package com.knowy.core.domain;

/**
 * A composite record that encapsulates both pagination and sorting configuration.
 * <p>
 * This object is typically used as a request parameter to define how a data set should be partitioned and ordered in a
 * single operation.
 * </p>
 *
 * @param <T>   the type of the properties used for sorting criteria
 * @param page  the pagination settings (index and size)
 * @param order the sorting settings (properties, strategy, and direction)
 */
public record Pagination<T>(Page page, Order<T> order) {
}
