package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.persistence.adapter.jpa.dao.JpaCourseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.DocumentationEntity;
import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaLessonMapper implements EntityMapper<Lesson, LessonEntity> {

	private final JpaDocumentationMapper jpaDocumentationMapper;
	private final JpaExerciseMapper jpaExerciseMapper;
	private final JpaCourseDao jpaCourseDao;
	private final JpaLessonDao jpaLessonDao;

	public JpaLessonMapper(
		JpaDocumentationMapper jpaDocumentationMapper,
		JpaExerciseMapper jpaExerciseMapper,
		JpaCourseDao jpaCourseDao,
		JpaLessonDao jpaLessonDao
	) {
		this.jpaDocumentationMapper = jpaDocumentationMapper;
		this.jpaExerciseMapper = jpaExerciseMapper;
		this.jpaCourseDao = jpaCourseDao;
		this.jpaLessonDao = jpaLessonDao;
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
	public LessonEntity toEntity(Lesson domain) throws KnowyInconsistentDataException {
		List<ExerciseEntity> exerciseEntities = new ArrayList<>();
		for (Exercise exercise : domain.exercises()) {
			ExerciseEntity entity = jpaExerciseMapper.toEntity(exercise);
			exerciseEntities.add(entity);
		}

		return new LessonEntity(
			domain.id(),
			jpaCourseDao.findById(domain.courseId()).orElseThrow(
				() -> new KnowyInconsistentDataException("Course with ID " + domain.courseId() + " not found")),
			jpaLessonDao.findById(domain.nextLessonId()).orElseThrow(
				() -> new KnowyInconsistentDataException(
					"Next Lesson with ID " + domain.nextLessonId() + " not found")
			),
			domain.title(),
			domain.explanation(),
			domain.documentations().stream()
				.map(jpaDocumentationMapper::toEntity)
				.toList(),
			exerciseEntities
		);
	}

	public <T extends LessonUnidentifiedData> LessonEntity toEntity(
		T domain,
		CourseEntity course,
		LessonEntity nextLesson
	) throws KnowyInconsistentDataException {
		LessonEntity lesson = new LessonEntity();
		lesson.setId(null);
		lesson.setCourse(course);
		lesson.setNextLesson(nextLesson);
		lesson.setTitle(domain.title());
		lesson.setExplanation(domain.explanation());

		List<DocumentationEntity> documentationEntities = new ArrayList<>();
		for (DocumentationData doc : domain.documentations()) {
			documentationEntities.add(jpaDocumentationMapper.toEntity(doc, lesson));
		}
		lesson.setDocumentations(documentationEntities);

		List<ExerciseEntity> exerciseEntities = new ArrayList<>();
		for (ExerciseUnidentifiedData exercise : domain.exercises()) {
			exerciseEntities.add(jpaExerciseMapper.toEntity(exercise, lesson));
		}
		lesson.setExercises(exerciseEntities);

		return lesson;
	}
}
