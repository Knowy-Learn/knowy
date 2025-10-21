package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserExerciseDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseId;
import com.knowy.persistence.adapter.jpa.mapper.JpaExerciseMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaOptionMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserExerciseMapper;

import java.util.List;
import java.util.Optional;

public class JpaUserExerciseRepository implements UserExerciseRepository {

	private final JpaUserExerciseDao jpaUserExerciseDao;
	private final JpaUserDao jpaUserDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final JpaLessonDao jpaLessonDao;

	public JpaUserExerciseRepository(
		JpaUserExerciseDao jpaUserExerciseDao,
		JpaUserDao jpaUserDao,
		JpaExerciseDao jpaExerciseDao,
		JpaLessonDao jpaLessonDao
	) {
		this.jpaUserExerciseDao = jpaUserExerciseDao;
		this.jpaUserDao = jpaUserDao;
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaLessonDao = jpaLessonDao;
	}

	@Override
	public UserExercise save(UserExercise userExercise) throws KnowyInconsistentDataException {
		JpaUserExerciseMapper jpaUserExerciseMapper = jpaUserExerciseMapper();

		PublicUserExerciseEntity userSaved = jpaUserExerciseDao.save(jpaUserExerciseMapper.toEntity(userExercise));
		return jpaUserExerciseMapper.toDomain(userSaved);
	}

	@Override
	public Optional<UserExercise> findById(int userId, int exerciseId) {
		JpaUserExerciseMapper jpaUserExerciseMapper = jpaUserExerciseMapper();

		return jpaUserExerciseDao.findById(new PublicUserExerciseId(userId, exerciseId))
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public List<UserExercise> findAll() {
		JpaUserExerciseMapper jpaUserExerciseMapper = jpaUserExerciseMapper();

		return jpaUserExerciseDao.findAll()
			.stream()
			.map(jpaUserExerciseMapper::toDomain)
			.toList();
	}

	@Override
	public List<UserExercise> findAllByUserIdAndLessonId(int userId, int lessonId) {
		JpaUserExerciseMapper jpaUserExerciseMapper = jpaUserExerciseMapper();

		return jpaUserExerciseDao.findAllByUserIdAndLessonId(userId, lessonId).stream()
			.map(jpaUserExerciseMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<UserExercise> findNextExerciseByLessonId(int userId, int lessonId) {
		JpaUserExerciseMapper jpaUserExerciseMapper = jpaUserExerciseMapper();

		return jpaUserExerciseDao.findNextExerciseByLessonId(userId, lessonId)
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public Optional<UserExercise> findNextExerciseByUserId(int userId) {
		JpaUserExerciseMapper jpaUserExerciseMapper = jpaUserExerciseMapper();

		return jpaUserExerciseDao.findNextExerciseByUserId(userId)
			.map(jpaUserExerciseMapper::toDomain);
	}

	private JpaUserExerciseMapper jpaUserExerciseMapper() {
		return new JpaUserExerciseMapper(
			jpaUserDao,
			jpaExerciseDao,
			new JpaExerciseMapper(
				new JpaOptionMapper(jpaExerciseDao),
				jpaLessonDao
			)
		);
	}
}
