package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Exercise;
import com.knowy.core.port.ExerciseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaExerciseMapper;

import java.util.Optional;

public class JpaExerciseRepository implements ExerciseRepository {

	private final JpaExerciseDao jpaExerciseDao;
	private final JpaExerciseMapper jpaExerciseMapper;

	public JpaExerciseRepository(JpaExerciseDao jpaExerciseDao, JpaExerciseMapper jpaExerciseMapper) {
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaExerciseMapper = jpaExerciseMapper;
	}

	@Override
	public Optional<Exercise> findById(int id) {
		return jpaExerciseDao.findById(id).map(jpaExerciseMapper::toDomain);
	}
}
