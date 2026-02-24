package com.knowy.server.api.mapper;

import com.knowy.core.domain.ProgressStatus;
import com.knowy.server.api.dto.ProgressStatusEnum;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper utility to convert CourseStatus DTO enums to Domain entities.
 */
public class ProgressStatusMapper {

	/**
	 * Converts a set of DTO status enums to an unmodifiable set of domain status entities.
	 *
	 * @param statusEnums the set of DTO enums to map.
	 * @return an unmodifiable set of mapped CourseStatus, or an empty set if input is null or empty.
	 */
	public Set<ProgressStatus> toDomain(Set<ProgressStatusEnum> statusEnums) {
		if (statusEnums == null || statusEnums.isEmpty()) {
			return Set.of();
		}

		return statusEnums.stream()
			.map(Enum::name)
			.map(ProgressStatus::fromString)
			.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Converts a domain ProgressStatus entity to a DTO status enum.
	 *
	 * @param progressStatus the domain status entity to map.
	 * @return the corresponding DTO enum.
	 * @throws NullPointerException if progressStatus is null.
	 */
	public ProgressStatusEnum fromDomain(ProgressStatus progressStatus) {
		Objects.requireNonNull(progressStatus);
		return ProgressStatusEnum.fromValue(progressStatus.name().toLowerCase());
	}
}
