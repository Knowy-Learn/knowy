package com.knowy.server.api.mapper;

import com.knowy.core.domain.CourseStatus;
import com.knowy.server.api.dto.CourseStatusEnum;

import java.util.Set;
import java.util.stream.Collectors;

public class CourseStatusEnumMapper {

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
