package com.knowy.persistence.adapter.jpa.entity;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "public_user_lesson")
@IdClass(PublicUserLessonIdEntity.class)
public class PublicUserLessonEntity {

	@Id
	@Column(name = "id_public_user", nullable = false)
	private Integer userId;
	@Id
	@Column(name = "id_lesson", nullable = false)
	private Integer lessonId;
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
	@Column(name = "status", nullable = false)
	private String status;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_public_user", referencedColumnName = "id", insertable = false, updatable = false)
	private PublicUserEntity publicUserEntity;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_lesson", referencedColumnName = "id", insertable = false, updatable = false)
	private LessonEntity lessonEntity;

	public PublicUserLessonEntity(
		Integer userId,
		Integer lessonId,
		LocalDate startDate,
		String status,
		PublicUserEntity publicUserEntity,
		LessonEntity lessonEntity
	) {
		this.userId = userId;
		this.lessonId = lessonId;
		this.startDate = startDate;
		setStatus(status);
		this.publicUserEntity = publicUserEntity;
		this.lessonEntity = lessonEntity;
	}

	public void setStatus(String status) {
		if (status == null) {
			throw new KnowyIllegalArgumentRuntimeException(
				"status cannot be null. Allowed values: COMPLETED, IN_PROGRESS, PENDING"
			);
		}
		if (!status.equalsIgnoreCase("COMPLETED") &&
			!status.equalsIgnoreCase("IN_PROGRESS") &&
			!status.equalsIgnoreCase("PENDING")) {
			throw new KnowyIllegalArgumentRuntimeException(
				"Invalid status value: " + status + ". Allowed values: COMPLETED, IN_PROGRESS, PENDING"
			);
		}
		this.status = status;
	}
}
