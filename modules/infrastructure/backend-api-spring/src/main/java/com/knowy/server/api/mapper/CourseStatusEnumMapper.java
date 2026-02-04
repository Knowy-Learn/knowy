package com.knowy.server.api.mapper;

import com.knowy.core.domain.CourseStatus;
import com.knowy.server.api.dto.CourseStatusEnum;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper utility to convert CourseStatus DTO enums to Domain entities.
 */
public class CourseStatusEnumMapper {

	/**
	 * Converts a set of DTO status enums to an unmodifiable set of domain status entities.
	 *
	 * @param statusEnums the set of DTO enums to map.
	 * @return an unmodifiable set of mapped CourseStatus, or an empty set if input is null or empty.
	 */
	public Set<CourseStatus> toDomain(Set<CourseStatusEnum> statusEnums) {
		if (statusEnums == null || statusEnums.isEmpty()) {
			return Set.of();
		}

		return statusEnums.stream()
			.map(Enum::name)
			.map(CourseStatus::fromString)
			.collect(Collectors.toUnmodifiableSet());
	}
}
