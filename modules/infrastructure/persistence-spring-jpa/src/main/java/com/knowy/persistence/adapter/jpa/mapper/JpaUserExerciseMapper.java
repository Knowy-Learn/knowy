package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.UserExercise;
import com.knowy.core.exception.KnowyExerciseNotFoundException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseId;

public class JpaUserExerciseMapper implements EntityMapper<UserExercise, PublicUserExerciseEntity> {
	private final JpaUserDao jpaUserDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final JpaExerciseMapper jpaExerciseMapper;

	public JpaUserExerciseMapper(JpaUserDao jpaUserDao, JpaExerciseDao jpaExerciseDao, JpaExerciseMapper jpaExerciseMapper) {
		this.jpaUserDao = jpaUserDao;
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaExerciseMapper = jpaExerciseMapper;
	}

	@Override
	public UserExercise toDomain(PublicUserExerciseEntity entity) {
		return new UserExercise(
			entity.getPublicUserEntity().getId(),
			jpaExerciseMapper.toDomain(entity.getExerciseEntity()),
			entity.getRate(),
			entity.getNextReview()
		);
	}

	@Override
	public PublicUserExerciseEntity toEntity(UserExercise domain) throws KnowyUserNotFoundException,
		KnowyExerciseNotFoundException {
		return new PublicUserExerciseEntity(
			new PublicUserExerciseId(domain.userId(), domain.exercise().id()),
			domain.rate(),
			domain.nextReview(),
			jpaUserDao.findById(domain.userId())
				.orElseThrow(() -> new KnowyUserNotFoundException("User with ID " + domain.userId() + " not found")),
			jpaExerciseDao.findById(domain.exercise().id())
				.orElseThrow(() -> new KnowyExerciseNotFoundException("Exercise with ID: " + domain.exercise().id() +
					" not found"))
		);
	}
}
