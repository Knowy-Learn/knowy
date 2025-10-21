package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Lesson;
import com.knowy.core.port.LessonRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCourseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserLessonDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaDocumentationMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaExerciseMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaLessonMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaOptionMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaLessonRepository implements LessonRepository {

	private final JpaLessonDao jpaLessonDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final JpaCourseDao jpaCourseDao;

	public JpaLessonRepository(JpaLessonDao jpaLessonDao, JpaUserLessonDao jpaUserLessonDao, JpaExerciseDao jpaExerciseDao, JpaCourseDao jpaCourseDao) {
		this.jpaLessonDao = jpaLessonDao;
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaCourseDao = jpaCourseDao;
	}

	@Override
	public Optional<Lesson> findById(Integer id) {
		JpaLessonMapper jpaLessonMapper = jpaLessonMapper();

		return jpaLessonDao.findById(id).map(jpaLessonMapper::toDomain);
	}


	@Override
	public Set<Lesson> findAllWhereUserIsSubscribedTo(int userId) {
		JpaLessonMapper jpaLessonMapper = jpaLessonMapper();

		return jpaUserLessonDao.findByUserId(userId).stream()
			.map(PublicUserLessonEntity::getLessonEntity)
			.map(jpaLessonMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public List<Lesson> findAllByCourseId(Integer courseId) {
		JpaLessonMapper jpaLessonMapper = jpaLessonMapper();

		return jpaLessonDao.findByCourseId(courseId).stream()
			.map(jpaLessonMapper::toDomain)
			.toList();
	}
	private JpaLessonMapper jpaLessonMapper() {
		return new JpaLessonMapper(
			new JpaDocumentationMapper(jpaLessonDao),
			new JpaExerciseMapper(
				new JpaOptionMapper(jpaExerciseDao),
				jpaLessonDao
			),
			jpaCourseDao,
			jpaLessonDao
		);
	}

	@Override
	public int countByCourseId(Integer courseId) {
		return jpaLessonDao.countByCourseId(courseId);
	}
}
