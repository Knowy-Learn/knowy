package com.knowy.core.domain;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

	@Test
	void given_validData_when_creatingPagination_then_returnPaginationIsCreatedSuccessfully() {
		Page page = new Page(3, 10);
		Order order = new Order("date", Order.SortDirection.DESCENDING);
		Filter categories = new Filter("category", Filter.Operator.IN, List.of("Java", "Spring"));
		List<Filter> filters = List.of(categories);

		Pagination pagination = assertDoesNotThrow(() -> new Pagination(page, Optional.of(order), filters),
			"Pagination creation should not throw exception given valid parameters"
		);

		assertAll("Pagination properties",
			() -> assertEquals(page, pagination.page(), "Page should match expected value"),
			() -> assertEquals(filters, pagination.filters(), "Filters should match expected value"),
			() -> assertEquals(Optional.of(order), pagination.order(), "Order Optional should match"),
			() -> {
				Order actualOrder = pagination.order().orElseThrow();
				assertEquals("date", actualOrder.field(), "Order field should be 'date'");
				assertEquals(Order.SortDirection.DESCENDING, actualOrder.direction(), "Sort direction should be DESCENDING");
			}
		);
	}

	@Test
	void given_emptyValidData_when_creatingPagination_then_retu() {
		Page page = new Page(0, 20);
		Pagination pagination = new Pagination(page, Optional.empty(), List.of());

		assertAll("Pagination state validation",
			() -> assertTrue(pagination.filters().isEmpty(), "Filters list should be empty"),
			() -> assertTrue(pagination.order().isEmpty(), "Order shouldn't be present"),
			() -> assertEquals(page, pagination.page(), "Page object should match the input")
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

	@Test
	void given_invalidObject_when_creatingFilterForOperator_then_throwKnowyIllegalArgumentRuntimeException() {
		assertThrows(
			KnowyIllegalArgumentRuntimeException.class, () -> new Filter("name", Filter.Operator.IN, "name")
		);
	}
}
