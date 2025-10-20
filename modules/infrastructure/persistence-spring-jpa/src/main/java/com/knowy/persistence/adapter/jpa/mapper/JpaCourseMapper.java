package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.persistence.adapter.jpa.entity.CategoryEntity;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JpaCourseMapper implements EntityMapper<Course, CourseEntity> {

	private final JpaCategoryMapper jpaCategoryMapper;
	private final JpaLessonMapper jpaLessonMapper;

	public JpaCourseMapper(JpaCategoryMapper jpaCategoryMapper, @Lazy JpaLessonMapper jpaLessonMapper) {
		this.jpaCategoryMapper = jpaCategoryMapper;
		this.jpaLessonMapper = jpaLessonMapper;
	}

	@Override
	public Course toDomain(CourseEntity entity) {
		return new Course(
			entity.getId(),
			entity.getTitle(),
			entity.getDescription(),
			entity.getImage(),
			entity.getAuthor(),
			entity.getCreationDate(),
			entity.getLanguages().stream().map(jpaCategoryMapper::toDomain).collect(Collectors.toSet()),
			entity.getLessons().stream().map(jpaLessonMapper::toDomain).collect(Collectors.toSet())
		);
	}

	@Override
	public CourseEntity toEntity(Course domain) throws KnowyInconsistentDataException {
		List<LessonEntity> lessonEntities = new ArrayList<>();
		for (Lesson lesson : domain.lessons()) {
			LessonEntity entity = jpaLessonMapper.toEntity(lesson);
			lessonEntities.add(entity);
		}

		return new CourseEntity(
			domain.id(),
			domain.title(),
			domain.description(),
			domain.image(),
			domain.author(),
			domain.creationDate(),
			domain.categories().stream()
				.map(jpaCategoryMapper::toEntity)
				.toList(),
			lessonEntities
		);
	}

	public <T extends CourseUnidentifiedData> CourseEntity toEntity(T domain) throws KnowyInconsistentDataException {
		CourseEntity course = new CourseEntity();

		List<CategoryEntity> categories = new ArrayList<>();
		for (CategoryData categoryData : domain.categories()) {
			CategoryEntity entity = jpaCategoryMapper.toEntity(categoryData);
			categories.add(entity);
		}

		course.setId(null);
		course.setTitle(domain.title());
		course.setDescription(domain.description());
		course.setImage(domain.image());
		course.setAuthor(domain.author());
		course.setCreationDate(domain.creationDate());
		course.setLanguages(categories);
		course.setLessons(mapLessonsInReverse(domain.lessons(), course));

		return course;
	}

	private List<LessonEntity> mapLessonsInReverse(Collection<LessonUnidentifiedData> lessons, CourseEntity course) throws KnowyInconsistentDataException {
		List<LessonUnidentifiedData> reversed = new ArrayList<>(lessons).reversed();

		List<LessonEntity> lessonEntities = new ArrayList<>();
		LessonEntity nextLesson = null;
		for (LessonUnidentifiedData lesson : reversed) {
			LessonEntity lessonEntity = jpaLessonMapper.toEntity(lesson, course, nextLesson);
			lessonEntities.add(lessonEntity);
			nextLesson = lessonEntity;
		}

		return lessonEntities;
	}
}
