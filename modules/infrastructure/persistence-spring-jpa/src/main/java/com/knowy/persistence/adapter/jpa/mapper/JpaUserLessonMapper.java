package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyLessonNotFoundException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {UserLesson.class, PublicUserLessonEntity.class}
)
public class JpaUserLessonMapper implements EntityMapper<UserLesson, PublicUserLessonEntity> {

	private final JpaLessonMapper jpaLessonMapper;
	private final JpaUserDao jpaUserDao;
	private final JpaLessonDao jpaLessonDao;

	public JpaUserLessonMapper(JpaLessonMapper jpaLessonMapper, JpaUserDao jpaUserDao, JpaLessonDao jpaLessonDao) {
		this.jpaLessonMapper = jpaLessonMapper;
		this.jpaUserDao = jpaUserDao;
		this.jpaLessonDao = jpaLessonDao;
	}

	@Override
	public UserLesson toDomain(PublicUserLessonEntity entity) {
		return new UserLesson(
			entity.getPublicUserEntity().getId(),
			jpaLessonMapper.toDomain(entity.getLessonEntity()),
			entity.getStartDate(),
			UserLesson.ProgressStatus
				.valueOf(entity.getStatus().toUpperCase())
		);
	}

	@Override
	public PublicUserLessonEntity toEntity(UserLesson domain) throws KnowyUserNotFoundException, KnowyLessonNotFoundException {
		return new PublicUserLessonEntity(
			domain.userId(),
			domain.lesson().id(),
			domain.startDate(),
			domain.status().name().toLowerCase(),
			jpaUserDao.findById(domain.userId())
				.orElseThrow(() -> new KnowyUserNotFoundException("User with ID: " + domain.userId() + " not found")),
			jpaLessonDao.findById(domain.lesson().id())
				.orElseThrow(() -> new KnowyLessonNotFoundException("Lesson with ID: " + domain.lesson().id() +
					" not found"))
		);
	}
}
