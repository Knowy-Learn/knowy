package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaUserLessonDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonIdEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserLessonMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JpaUserLessonRepository implements UserLessonRepository {

	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaUserLessonMapper jpaUserLessonMapper;

	public JpaUserLessonRepository(JpaUserLessonDao jpaUserLessonDao, JpaUserLessonMapper jpaUserLessonMapper) {
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaUserLessonMapper = jpaUserLessonMapper;
	}

	@Override
	public boolean existsByUserIdAndLessonId(Integer userId, Integer lessonId) {
		return jpaUserLessonDao.existsByUserIdAndLessonId(userId, lessonId);
	}

	@Override
	public List<Integer> findCourseIdsByUserId(Integer userId) {
		return jpaUserLessonDao.findCourseIdsByUserId(userId);
	}

	@Override
	public boolean existsById(int userId, int lessonId) {
		return jpaUserLessonDao.existsById(new PublicUserLessonIdEntity(userId, lessonId));
	}

	@Override
	public UserLesson save(UserLesson userLesson) throws KnowyInconsistentDataException {
		PublicUserLessonEntity publicUserLessonEntity = jpaUserLessonDao.save(jpaUserLessonMapper.toEntity(userLesson));
		return jpaUserLessonMapper.toDomain(publicUserLessonEntity);
	}

	@Override
	public List<UserLesson> saveAll(Collection<UserLesson> userLessons) throws KnowyInconsistentDataException {
		List<PublicUserLessonEntity> lessonEntities = new ArrayList<>();
		for (UserLesson userLesson : userLessons) {
			lessonEntities.add(jpaUserLessonMapper.toEntity(userLesson));
		}

		return jpaUserLessonDao.saveAll(lessonEntities).stream()
			.map(jpaUserLessonMapper::toDomain)
			.toList();
	}

	@Override
	public int countByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, UserLesson.ProgressStatus status) {
		return jpaUserLessonDao.countByUserIdAndCourseIdAndStatus(userId, courseId, status.name().toLowerCase());
	}

	@Override
	public Optional<UserLesson> findById(int userId, int lessonId) {
		return jpaUserLessonDao.findById(new PublicUserLessonIdEntity(userId, lessonId))
			.map(jpaUserLessonMapper::toDomain);
	}

	@Override
	public List<UserLesson> findAllByUserIdAndCourseId(int userId, int courseId) {
		return jpaUserLessonDao.findAllByUserIdAndCourseId(userId, courseId).stream()
			.map(jpaUserLessonMapper::toDomain)
			.toList();
	}

	@Override
	public List<UserLesson> findAllWhereUserIsSubscribed(int userId) {
		return jpaUserLessonDao.findAllWhereUserIsSubscribed(userId);
	}
}
