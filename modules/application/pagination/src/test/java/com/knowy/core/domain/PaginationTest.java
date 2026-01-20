package com.knowy.core.domain;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

	@Test
	void given_validData_when_creatingPagination_then_returnPaginationIsCreatedSuccessfully() {
		Page expectedPage = new Page(3, 10);
		Order expectedOrder = new Order("date", Order.SortDirection.DESCENDING);
		Filter categories = new Filter("category", Filter.Operator.IN, List.of("Java", "Spring"));
		List<Filter> filters = List.of(categories);

		Pagination pagination = assertDoesNotThrow(() -> new Pagination(expectedPage, expectedOrder, filters),
			"Pagination creation should not throw exception given valid parameters"
		);

		assertAll("Pagination properties",
			() -> assertEquals(expectedPage, pagination.page(), "Page should match expected value"),
			() -> assertEquals(expectedOrder, pagination.order(), "Order should match expected value"),
			() -> assertEquals(filters, pagination.filters(), "Filters should match expected value"),
			() -> assertEquals("date", pagination.order().field(), "Order field should be 'date'"),
			() -> assertEquals(Order.SortDirection.DESCENDING, pagination.order().direction(), "Sort direction should be DESCENDING")
		);
	}

	@Test
	void given_invalidPageNumber_when_creatingPage_then_throwKnowyIllegalArgumentRuntimeException() {
		assertThrows(
			KnowyIllegalArgumentRuntimeException.class, () -> new Page(-25, 10)
		);
	}

	@Test
	void given_invalidPageSize_when_creatingPage_then_throwKnowyIllegalArgumentRuntimeException() {
		assertThrows(
			KnowyIllegalArgumentRuntimeException.class, () -> new Page(2, -5)
		);
	}
}
