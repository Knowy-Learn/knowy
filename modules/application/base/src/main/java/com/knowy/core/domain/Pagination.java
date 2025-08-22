package com.knowy.core.domain;

import com.knowy.core.exception.KnowyIllegalArgumentRuntimeException;


public record Pagination(int page, int size) {

	/**
	 * Represents pagination information with a page number and page size.
	 * <p>
	 * Ensures that the page number is non-negative and the page size is positive.
	 * </p>
	 *
	 * @param page the current page number, starting from 0
	 * @param size the number of items per page, must be greater than 0
	 */
	public Pagination {
		validatePage(page);
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
