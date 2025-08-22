package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Lesson;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {Lesson.class, LessonEntity.class}
)
public class JpaLessonMapper implements EntityMapper<Lesson, LessonEntity> {

	private final JpaDocumentationMapper jpaDocumentationMapper;
	private final JpaExerciseMapper jpaExerciseMapper;

	public JpaLessonMapper(
		JpaDocumentationMapper jpaDocumentationMapper,
		JpaExerciseMapper jpaExerciseMapper
	) {
		this.jpaDocumentationMapper = jpaDocumentationMapper;
		this.jpaExerciseMapper = jpaExerciseMapper;
	}

	@Override
	public Lesson toDomain(LessonEntity entity) {
		return new Lesson(
			entity.getId(),
			entity.getCourse().getId(),
			Optional.ofNullable(entity.getNextLesson())
				.map(LessonEntity::getId)
				.orElse(null),
			entity.getTitle(),
			entity.getExplanation(),
			entity.getDocumentations().stream()
				.map(jpaDocumentationMapper::toDomain)
				.collect(Collectors.toSet()),
			entity.getExercises().stream()
				.map(jpaExerciseMapper::toDomain)
				.collect(Collectors.toSet())
		);
	}

	@Override
	public LessonEntity toEntity(Lesson domain) {
		return null;
	}
}
