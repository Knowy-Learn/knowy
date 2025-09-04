package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.LessonBase;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaLessonBaseMapper implements EntityMapper<LessonBase, LessonEntity> {

	@Override
	public LessonBase toDomain(LessonEntity entity) {
		return new LessonBase(
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
	public LessonEntity toEntity(LessonBase domain) {
		return null;
	}
}
