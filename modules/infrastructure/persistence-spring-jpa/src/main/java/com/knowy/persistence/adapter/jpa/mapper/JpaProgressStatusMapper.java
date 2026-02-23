package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.ProgressStatus;
import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;

/**
 * Maps between JPA entity progress status strings and domain ProgressStatus enums.
 * Handles bidirectional conversion with status normalization.
 */
public class JpaProgressStatusMapper implements EntityMapper<ProgressStatus, String> {

	/**
	 * Converts a JPA entity status string to domain ProgressStatus enum.
	 *
	 * @param entity the status string from the database (COMPLETED, IN_PROGRESS, PENDING)
	 * @return the corresponding ProgressStatus enum value
	 * @throws KnowyIllegalArgumentRuntimeException if the status value is invalid
	 */
	@Override
	public ProgressStatus toDomain(String entity) {
		return switch (entity.toUpperCase()) {
			case "COMPLETED" -> ProgressStatus.COMPLETED;
			case "IN_PROGRESS" -> ProgressStatus.IN_PROGRESS;
			case "PENDING" -> ProgressStatus.NOT_STARTED;
			default -> throw new KnowyIllegalArgumentRuntimeException("Invalid entity value " + entity);
		};
	}

	/**
	 * Converts a domain ProgressStatus enum to JPA entity status string.
	 *
	 * @param domain the ProgressStatus enum value
	 * @return the corresponding database status string
	 */
	@Override
	public String toEntity(ProgressStatus domain) {
		return switch (domain) {
			case COMPLETED -> "COMPLETED";
			case IN_PROGRESS -> "IN_PROGRESS";
			case NOT_STARTED -> "PENDING";
		};
	}
}
