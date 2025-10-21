package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Exercise;
import com.knowy.core.domain.ExerciseUnidentifiedData;
import com.knowy.core.domain.Option;
import com.knowy.core.domain.OptionData;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import com.knowy.persistence.adapter.jpa.entity.OptionEntity;

import java.util.ArrayList;
import java.util.List;

public class JpaExerciseMapper implements EntityMapper<Exercise, ExerciseEntity> {

	private final JpaOptionMapper jpaOptionMapper;
	private final JpaLessonDao jpaLessonDao;

	public JpaExerciseMapper(JpaExerciseDao jpaExerciseDao, JpaLessonDao jpaLessonDao) {
		this.jpaOptionMapper = new JpaOptionMapper(jpaExerciseDao);
		this.jpaLessonDao = jpaLessonDao;
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
	public ExerciseEntity toEntity(Exercise domain) throws KnowyInconsistentDataException {
		List<OptionEntity> optionEntities = new ArrayList<>();
		for (Option option : domain.options()) {
			OptionEntity entity = jpaOptionMapper.toEntity(option);
			optionEntities.add(entity);
		}

		return new ExerciseEntity(
			domain.id(),
			jpaLessonDao.findById(domain.lessonId())
				.orElseThrow(() -> new KnowyInconsistentDataException(
					"Lesson with ID " + domain.lessonId() + " not found")
				),
			domain.statement(),
			optionEntities
		);
	}

	public <T extends ExerciseUnidentifiedData> ExerciseEntity toEntity(T domain, LessonEntity lesson) {
		ExerciseEntity exercise = new ExerciseEntity();
		exercise.setId(null);
		exercise.setLesson(lesson);
		exercise.setQuestion(domain.statement());

		List<OptionEntity> optionEntities = new ArrayList<>();
		for (OptionData option : domain.options()) {
			optionEntities.add(jpaOptionMapper.toEntity(option, exercise));
		}
		exercise.setOptions(optionEntities);

		return exercise;
	}
}
