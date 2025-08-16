package com.knowy.core.port;

import com.knowy.core.domain.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
	List<Course> findByIdIn(List<Integer> ids);

	List<Course> findAll();

	Optional<Course> findById(Integer id);

	List<Course> findAllRandom();
}
