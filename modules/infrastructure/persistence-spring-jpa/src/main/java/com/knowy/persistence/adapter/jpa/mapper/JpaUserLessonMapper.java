package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyLessonNotFoundException;
import com.knowy.core.user.exception.resource.KnowyUserNotFoundException;
import com.knowy.persistence.adapter.jpa.dao.JpaCourseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;

public class JpaUserLessonMapper implements EntityMapper<UserLesson, PublicUserLessonEntity> {

	private final JpaLessonMapper jpaLessonMapper;
	private final JpaUserDao jpaUserDao;
	private final JpaLessonDao jpaLessonDao;

	public JpaUserLessonMapper(
		JpaUserDao jpaUserDao,
		JpaLessonDao jpaLessonDao,
		JpaCourseDao jpaCourseDao,
		JpaExerciseDao jpaExerciseDao
	) {
		this.jpaLessonMapper = new JpaLessonMapper(jpaCourseDao, jpaLessonDao, jpaExerciseDao);
		this.jpaUserDao = jpaUserDao;
		this.jpaLessonDao = jpaLessonDao;
	}

	@Override
	public UserLesson toDomain(PublicUserLessonEntity entity) {
		var jpaProgressStatusMapper = new JpaProgressStatusMapper();

		return new UserLesson(
			entity.getPublicUserEntity().getId(),
			jpaLessonMapper.toDomain(entity.getLessonEntity()),
			entity.getStartDate(),
			jpaProgressStatusMapper.toDomain(entity.getStatus())
		);
	}

	@Override
	public PublicUserLessonEntity toEntity(UserLesson domain) throws KnowyUserNotFoundException, KnowyLessonNotFoundException {
		var jpaProgressStatusMapper = new JpaProgressStatusMapper();

		return new PublicUserLessonEntity(
			domain.userId(),
			domain.lesson().id(),
			domain.startDate(),
			jpaProgressStatusMapper.toEntity(domain.status()),
			jpaUserDao.findById(domain.userId())
				.orElseThrow(() -> new KnowyUserNotFoundException("User with ID: " + domain.userId() + " not found")),
			jpaLessonDao.findById(domain.lesson().id())
				.orElseThrow(() -> new KnowyLessonNotFoundException("Lesson with ID: " + domain.lesson().id() +
					" not found"))
		);
	}
}
