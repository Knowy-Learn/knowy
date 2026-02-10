package com.knowy.persistence.adapter.spring.mapper;

import com.knowy.core.domain.Order;
import com.knowy.core.domain.Pagination;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SpringPaginationMapper {

	public Pageable toPageable(Pagination pagination) {
		return PageRequest.of(
			pagination.page().number(),
			pagination.page().size(),
			pagination.order().map(this::toSort)
				.orElse(Sort.unsorted())
		);
	}

	public Sort toSort(Order order) {
		Sort.Direction direction = order.direction() == Order.SortDirection.ASCENDING
			? Sort.Direction.ASC
			: Sort.Direction.DESC;

		return Sort.by(direction, order.field());
	}

	public Pageable toPageableWithoutSort(Pagination pagination) {
		return PageRequest.of(
			pagination.page().number(),
			pagination.page().size()
		);
	}
}
