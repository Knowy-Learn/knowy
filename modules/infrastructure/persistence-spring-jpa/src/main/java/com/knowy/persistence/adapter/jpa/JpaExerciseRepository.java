package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Exercise;
import com.knowy.core.port.ExerciseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaExerciseMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaOptionMapper;

import java.util.Optional;

public class JpaExerciseRepository implements ExerciseRepository {

	private final JpaExerciseDao jpaExerciseDao;
	private final JpaLessonDao jpaLessonDao;

	public JpaExerciseRepository(JpaExerciseDao jpaExerciseDao, JpaLessonDao jpaLessonDao) {
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaLessonDao = jpaLessonDao;
	}

	@Override
	public Optional<Exercise> findById(int id) {
		JpaExerciseMapper jpaExerciseMapper = new JpaExerciseMapper(
			new JpaOptionMapper(jpaExerciseDao),
			jpaLessonDao
		);
		return jpaExerciseDao.findById(id).map(jpaExerciseMapper::toDomain);
	}
}
