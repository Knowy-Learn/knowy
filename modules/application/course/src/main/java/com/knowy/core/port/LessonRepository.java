package com.knowy.core.port;

import com.knowy.core.domain.Lesson;

import java.util.List;
import java.util.Optional;


public interface LessonRepository {
	List<Lesson> findByCourseId(Integer courseId);

	int countByCourseId(Integer courseId);

	Optional<Lesson> findById(Integer id);
}
