package com.knowy.core.domain;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;

/**
 * Represents pagination metadata for data retrieval operations.
 * <p>
 * This record ensures that pagination parameters are within valid logical bounds: non-negative page numbers and
 * strictly positive page sizes.
 * </p>
 *
 * @param number the zero-based index of the page to retrieve (must be {@code >= 0}).
 * @param size   the maximum number of records to include in a single page (must be {@code > 0}).
 */
public record Page(int number, int size) {

	/**
	 * Compact constructor to validate pagination constraints. * @param number the zero-based index of the page.
	 *
	 * @param size the maximum number of records per page.
	 * @throws KnowyIllegalArgumentRuntimeException if {@code number} is negative or {@code size} is less than 1.
	 */
	public Page {
		validatePage(number);
		validateSize(size);
	}

	private static void validatePage(int page) {
		if (page < 0) {
			throw new KnowyIllegalArgumentRuntimeException("Page cannot be negative: " + page);
		}
		if (page >= 100_000) {
			throw new KnowyIllegalArgumentRuntimeException("Page exceeds the maximum limit of 100,000: " + page);
		}
	}

	private static void validateSize(int size) {
		if (size <= 0) {
			throw new KnowyIllegalArgumentRuntimeException("Size must be positive: " + size);
		}
	}
}
