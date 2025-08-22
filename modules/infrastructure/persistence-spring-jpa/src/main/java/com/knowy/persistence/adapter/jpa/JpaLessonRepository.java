package com.knowy.persistence.adapter.jpa;

import com.knowy.core.port.LessonRepository;
import com.knowy.core.domain.Lesson;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserLessonDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaLessonMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaLessonRepository implements LessonRepository {

	private final JpaLessonDao jpaLessonDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaLessonMapper jpaLessonMapper;

	public JpaLessonRepository(JpaLessonDao jpaLessonDao, JpaUserLessonDao jpaUserLessonDao, JpaLessonMapper jpaLessonMapper) {
		this.jpaLessonDao = jpaLessonDao;
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaLessonMapper = jpaLessonMapper;
	}

	@Override
	public Optional<Lesson> findById(Integer id) {
		return jpaLessonDao.findById(id).map(jpaLessonMapper::toDomain);
	}

	@Override
	public Set<Lesson> findAllWhereUserIsSubscribedTo(int userId) {
		return jpaUserLessonDao.findByUserId(userId).stream()
			.map(PublicUserLessonEntity::getLessonEntity)
			.map(jpaLessonMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public List<Lesson> findAllByCourseId(Integer courseId) {
		return jpaLessonDao.findByCourseId(courseId).stream()
			.map(jpaLessonMapper::toDomain)
			.toList();
	}

	@Override
	public int countByCourseId(Integer courseId) {
		return jpaLessonDao.countByCourseId(courseId);
	}
}
