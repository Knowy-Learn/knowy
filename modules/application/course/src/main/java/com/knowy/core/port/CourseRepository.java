package com.knowy.core.port;

import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
	List<Course> findAllById(List<Integer> ids) throws KnowyInconsistentDataException;

	List<Course> findAll() throws KnowyInconsistentDataException;

	Optional<Course> findById(Integer id) throws KnowyInconsistentDataException;

	List<Course> findAllRandom() throws KnowyInconsistentDataException;
}
