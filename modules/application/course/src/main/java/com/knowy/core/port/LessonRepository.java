package com.knowy.core.port;

import com.knowy.core.domain.Lesson;

import java.util.List;
import java.util.Optional;


public interface LessonRepository {
	List<Lesson> findAllByCourseId(Integer courseId);

	Optional<Lesson> findById(Integer id);

	List<Lesson> findAllByCourseIdAndUserUnsubscribed(int userId, int courseId);

	int countByCourseId(Integer courseId);
}
