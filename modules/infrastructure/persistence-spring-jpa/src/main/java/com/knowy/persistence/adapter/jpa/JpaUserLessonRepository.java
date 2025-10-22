package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonIdEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserLessonMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class JpaUserLessonRepository implements UserLessonRepository {

	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaLessonDao jpaLessonDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final JpaCourseDao jpaCourseDao;
	private final JpaUserDao jpaUserDao;

	public JpaUserLessonRepository(
		JpaUserLessonDao jpaUserLessonDao,
		JpaLessonDao jpaLessonDao,
		JpaExerciseDao jpaExerciseDao,
		JpaCourseDao jpaCourseDao, JpaUserDao jpaUserDao
	) {
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaLessonDao = jpaLessonDao;
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaCourseDao = jpaCourseDao;
		this.jpaUserDao = jpaUserDao;
	}

	@Override
	public List<Integer> findCourseIdsByUserId(Integer userId) {
		return jpaUserLessonDao.findCourseIdsByUserId(userId);
	}

	@Override
	public UserLesson save(UserLesson userLesson) throws KnowyInconsistentDataException {
		JpaUserLessonMapper jpaUserLessonMapper = getJpaUserLessonMapper();

		PublicUserLessonEntity publicUserLessonEntity = jpaUserLessonDao.save(jpaUserLessonMapper.toEntity(userLesson));
		return jpaUserLessonMapper.toDomain(publicUserLessonEntity);
	}

	@Override
	public List<UserLesson> saveAll(Collection<UserLesson> userLessons) throws KnowyInconsistentDataException {
		JpaUserLessonMapper jpaUserLessonMapper = getJpaUserLessonMapper();

		List<PublicUserLessonEntity> lessonEntities = new ArrayList<>();
		for (UserLesson userLesson : userLessons) {
			lessonEntities.add(jpaUserLessonMapper.toEntity(userLesson));
		}

		return jpaUserLessonDao.saveAll(lessonEntities).stream()
			.map(jpaUserLessonMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<UserLesson> findById(int userId, int lessonId) {
		JpaUserLessonMapper jpaUserLessonMapper = getJpaUserLessonMapper();

		return jpaUserLessonDao.findById(new PublicUserLessonIdEntity(userId, lessonId))
			.map(jpaUserLessonMapper::toDomain);
	}

	@Override
	public List<UserLesson> findAllByUserIdAndCourseId(int userId, int courseId) {
		JpaUserLessonMapper jpaUserLessonMapper = getJpaUserLessonMapper();

		return jpaUserLessonDao.findAllByUserIdAndCourseId(userId, courseId).stream()
			.map(jpaUserLessonMapper::toDomain)
			.toList();
	}

	@Override
	public List<UserLesson> findAllWhereUserIsSubscribed(int userId) {
		JpaUserLessonMapper jpaUserLessonMapper = getJpaUserLessonMapper();

		return jpaUserLessonDao.findAllWhereUserIsSubscribed(userId).stream()
			.map(jpaUserLessonMapper::toDomain)
			.toList();
	}

	private JpaUserLessonMapper getJpaUserLessonMapper() {
		return new JpaUserLessonMapper(jpaUserDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao);
	}
}
