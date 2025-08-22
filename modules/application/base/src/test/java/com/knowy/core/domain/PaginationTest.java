package com.knowy.core.domain;

import com.knowy.core.exception.KnowyIllegalArgumentRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

	@Test
	void given_validData_when_creatingPagination_then_returnPaginationIsCreatedSuccessfully() {
		int page = 3;
		int size = 10;

		Pagination pagination = assertDoesNotThrow(
			() -> new Pagination(page, size)
		);
		assertAll(
			() -> assertFalse(pagination.page() < 0),
			() -> assertFalse(pagination.size() <= 0)
		);
	}

	@Test
	void given_InvalidPage_when_creatingPagination_then_throwKnowyIllegalArgumentRuntimeException() {
		int page = -25;
		int size = 10;

		assertThrows(
			KnowyIllegalArgumentRuntimeException.class,
			() -> new Pagination(page, size)
		);
	}

	@Test
	void given_InvalidPageSize_when_creatingPagination_then_throwKnowyIllegalArgumentRuntimeException() {
		int page = 2;
		int size = -5;

		assertThrows(
			KnowyIllegalArgumentRuntimeException.class,
			() -> new Pagination(page, size)
		);
	}

}
