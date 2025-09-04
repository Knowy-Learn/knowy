package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyDataAccessException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.UserExerciseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaUserExerciseDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseId;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserExerciseMapper;

import java.util.List;
import java.util.Optional;

public class JpaUserExerciseRepository implements UserExerciseRepository {

	private final JpaUserExerciseDao jpaUserExerciseDao;
	private final JpaUserExerciseMapper jpaUserExerciseMapper;

	public JpaUserExerciseRepository(JpaUserExerciseDao jpaUserExerciseDao, JpaUserExerciseMapper jpaUserExerciseMapper) {
		this.jpaUserExerciseDao = jpaUserExerciseDao;
		this.jpaUserExerciseMapper = jpaUserExerciseMapper;
	}

	@Override
	public UserExercise save(UserExercise userExercise)
		throws KnowyInconsistentDataException {

		PublicUserExerciseEntity userSaved = jpaUserExerciseDao.save(jpaUserExerciseMapper.toEntity(userExercise));
		return jpaUserExerciseMapper.toDomain(userSaved);
	}

	@Override
	public Optional<UserExercise> findById(int userId, int exerciseId) {
		return jpaUserExerciseDao.findById(new PublicUserExerciseId(userId, exerciseId))
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public List<UserExercise> findAll() {
		return jpaUserExerciseDao.findAll()
			.stream()
			.map(jpaUserExerciseMapper::toDomain)
			.toList();
	}

	@Override
	public List<UserExercise> findAllByUserIdAndLessonId(int userId, int lessonId) throws KnowyDataAccessException {
		return jpaUserExerciseDao.findAllByUserIdAndLessonId(userId, lessonId).stream()
			.map(jpaUserExerciseMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<UserExercise> findNextExerciseByLessonId(int userId, int lessonId) {
		return jpaUserExerciseDao.findNextExerciseByLessonId(userId, lessonId)
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public Optional<UserExercise> findNextExerciseByUserId(int userId) {
		return jpaUserExerciseDao.findNextExerciseByUserId(userId)
			.map(jpaUserExerciseMapper::toDomain);
	}
}
