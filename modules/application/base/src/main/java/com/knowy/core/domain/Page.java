package com.knowy.core.domain;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;

/**
 * Represents pagination metadata for data retrieval operations.
 * <p>
 * This record ensures that pagination parameters are within valid logical bounds: non-negative page numbers and
 * strictly positive page sizes.
 * </p>
 *
 * @param number the zero-based index of the page to retrieve.
 * @param size   the maximum number of records to include in a single page.
 */
public record Page(int number, int size) {

	/**
	 * Compact constructor to validate pagination constraints.
	 *
	 * @throws KnowyIllegalArgumentRuntimeException if number is negative or size is less than 1
	 */
	public Page {
		validatePage(number);
		validateSize(size);
	}

	private static void validatePage(int page) {
		if (page < 0) {
			throw new KnowyIllegalArgumentRuntimeException("Page cannot be negative: " + page);
		}
	}

	private static void validateSize(int size) {
		if (size <= 0) {
			throw new KnowyIllegalArgumentRuntimeException("Size must be positive: " + size);
		}
	}
}
