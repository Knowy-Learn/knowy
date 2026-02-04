package com.knowy.server.api.mapper;

import com.knowy.core.domain.Order;
import com.knowy.server.api.dto.DirectionEnum;
import com.knowy.server.api.dto.OrderEnum;

import java.util.Optional;

/**
 * Mapper utility to transform API sorting preferences into domain Order entities.
 */
public class OrderMapper {

	/**
	 * Maps DTO order and direction enums to a domain Order object.
	 *
	 * @param orderEnum     the field to order by (defaults to TITLE if null).
	 * @param directionEnum the direction of the sort (defaults to ASCENDING if null).
	 * @return a domain Order instance with the mapped field and direction.
	 */
	public Order toDomain(OrderEnum orderEnum, DirectionEnum directionEnum) {
		Order.SortDirection direction = mapDirection(directionEnum);

		OrderEnum safeOrder = (orderEnum != null) ? orderEnum : OrderEnum.TITLE;

		return switch (safeOrder) {
			case CREATED_AT -> new Order("creationDate", direction);
			case AUTHOR -> new Order("author", direction);
			default -> new Order("title", direction);
		};
	}

	private Order.SortDirection mapDirection(DirectionEnum directionEnum) {
		return Optional.ofNullable(directionEnum)
			.map(direction -> Order.SortDirection.fromString(direction.name()))
			.orElse(Order.SortDirection.ASCENDING);
	}
}