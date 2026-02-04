package com.knowy.server.api.mapper;

import com.knowy.core.domain.Order;
import com.knowy.server.api.dto.DirectionEnum;
import com.knowy.server.api.dto.OrderEnum;

import java.util.Optional;

public class OrderMapper {

    public Order toDomain(OrderEnum orderEnum, DirectionEnum directionEnum) {
        Order.SortDirection direction = mapDirection(directionEnum);
        
        OrderEnum safeOrder = (orderEnum != null) ? orderEnum : OrderEnum.TITLE;

        return switch (safeOrder) {
            case CREATED_AT -> new Order("creationDate", direction);
            case AUTHOR     -> new Order("author", direction);
            default         -> new Order("title", direction);
        };
    }

    private Order.SortDirection mapDirection(DirectionEnum directionEnum) {
        return Optional.ofNullable(directionEnum)
                .map(direction -> Order.SortDirection.fromString(direction.name()))
                .orElse(Order.SortDirection.ASCENDING);
    }
}