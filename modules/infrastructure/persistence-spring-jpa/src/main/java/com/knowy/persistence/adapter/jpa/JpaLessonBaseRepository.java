package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.LessonInfo;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.port.LessonBaseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaLessonBaseMapper;

import java.util.Optional;

public class JpaLessonBaseRepository implements LessonBaseRepository {

	private final JpaLessonDao jpaLessonDao;
	private final JpaLessonBaseMapper jpaLessonBaseMapper;

	public JpaLessonBaseRepository(JpaLessonDao jpaLessonDao, JpaLessonBaseMapper jpaLessonBaseMapper) {
		this.jpaLessonDao = jpaLessonDao;
		this.jpaLessonBaseMapper = jpaLessonBaseMapper;
	}

	@Override
	public Optional<LessonInfo> findById(int lessonId) throws KnowyDataAccessException {
		return jpaLessonDao.findById(lessonId).map(jpaLessonBaseMapper::toDomain);
	}
}
