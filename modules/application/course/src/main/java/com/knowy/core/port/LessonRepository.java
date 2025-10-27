package com.knowy.core.port;

import com.knowy.core.domain.Lesson;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface LessonRepository {
	List<Lesson> findAllByCourseId(Integer courseId);

	Optional<Lesson> findById(Integer id);

	Set<Lesson> findAllWhereUserIsSubscribedTo(int userId);
}