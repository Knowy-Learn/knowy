package com.knowy.server.infrastructure.adapters.persistence;

import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.server.infrastructure.adapters.persistence.dao.JpaCourseDao;
import com.knowy.server.infrastructure.adapters.persistence.mapper.JpaCourseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaCourseRepository implements CourseRepository {

	private final JpaCourseDao jpaCourseDao;
	private final JpaCourseMapper jpaCourseMapper;

	public JpaCourseRepository(JpaCourseDao jpaCourseDao, JpaCourseMapper jpaCourseMapper) {
		this.jpaCourseDao = jpaCourseDao;
		this.jpaCourseMapper = jpaCourseMapper;
	}

	@Override
	public List<Course> findAllById(List<Integer> ids) {
		return jpaCourseDao.findAllById(ids)
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	@Override
	public List<Course> findAll() {
		return jpaCourseDao.findAll()
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<Course> findById(Integer id) {
		return jpaCourseDao.findById(id).map(jpaCourseMapper::toDomain);
	}

	@Override
	public List<Course> findAllRandom() {
		return jpaCourseDao.findAllRandom()
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	@Override
	public List<Course> findAllRandomUserIsNotSubscribed(int userId) {
		return jpaCourseDao.findAllRandomUserIsNotSubscribed(userId)
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}
}
