package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Exercise;
import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import org.springframework.stereotype.Component;

@Component
public class JpaExerciseMapper implements EntityMapper<Exercise, ExerciseEntity> {

	private final JpaOptionMapper jpaOptionMapper;

	public JpaExerciseMapper(JpaOptionMapper jpaOptionMapper) {
		this.jpaOptionMapper = jpaOptionMapper;
	}

	@Override
	public Exercise toDomain(ExerciseEntity entity) {
		return new Exercise(
			entity.getId(),
			entity.getLesson().getId(),
			entity.getQuestion(),
			entity.getOptions().stream()
				.map(jpaOptionMapper::toDomain)
				.toList()
		);
	}

	@Override
	public ExerciseEntity toEntity(Exercise domain) {
		return null;
	}
}
