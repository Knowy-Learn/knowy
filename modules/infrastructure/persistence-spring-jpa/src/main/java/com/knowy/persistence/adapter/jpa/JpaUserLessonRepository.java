package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonIdEntity;
import com.knowy.persistence.adapter.jpa.mapper.*;

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

	// FIXME: DELETE
	@Override
	public boolean existsByUserIdAndLessonId(Integer userId, Integer lessonId) {
		return jpaUserLessonDao.existsByUserIdAndLessonId(userId, lessonId);
	}

	@Override
	public List<Integer> findCourseIdsByUserId(Integer userId) {
		return jpaUserLessonDao.findCourseIdsByUserId(userId);
	}

	// FIXME: DELETE
	@Override
	public boolean existsById(int userId, int lessonId) {
		return jpaUserLessonDao.existsById(new PublicUserLessonIdEntity(userId, lessonId));
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

	// FIXME: DELETE
	@Override
	public int countByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, UserLesson.ProgressStatus status) {

		return jpaUserLessonDao.countByUserIdAndCourseIdAndStatus(userId, courseId, status.name().toLowerCase());
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
		return new JpaUserLessonMapper(
			new JpaLessonMapper(
				new JpaDocumentationMapper(jpaLessonDao),
				new JpaExerciseMapper(
					new JpaOptionMapper(jpaExerciseDao),
					jpaLessonDao
				),
				jpaCourseDao,
				jpaLessonDao
			),
			jpaUserDao,
			jpaLessonDao
		);
	}
}
