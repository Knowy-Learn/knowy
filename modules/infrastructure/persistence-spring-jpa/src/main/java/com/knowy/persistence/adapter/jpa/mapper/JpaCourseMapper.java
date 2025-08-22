package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Course;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {Course.class, CourseEntity.class}
)
public class JpaCourseMapper implements EntityMapper<Course, CourseEntity> {

	private final JpaCategoryMapper jpaCategoryMapper;
	private final JpaLessonMapper jpaLessonMapper;

	public JpaCourseMapper(JpaCategoryMapper jpaCategoryMapper,@Lazy JpaLessonMapper jpaLessonMapper) {
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
	public CourseEntity toEntity(Course domain) {
		return null;
	}
}
