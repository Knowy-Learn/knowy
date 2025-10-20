package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.LessonInfo;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;

import java.util.Optional;

public class JpaLessonBaseMapper implements EntityMapper<LessonInfo, LessonEntity> {

	@Override
	public LessonInfo toDomain(LessonEntity entity) {
		return new LessonInfo(
			entity.getId(),
			entity.getCourse().getId(),
			Optional.ofNullable(entity.getNextLesson())
				.map(LessonEntity::getId)
				.orElse(null),
			entity.getTitle(),
			entity.getExplanation()
		);
	}

	@Override
	public LessonEntity toEntity(LessonInfo domain) {
		throw new UnsupportedOperationException("Operation not implemented");
	}
}
