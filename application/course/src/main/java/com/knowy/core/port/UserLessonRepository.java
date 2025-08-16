package com.knowy.core.port;


import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;

import java.util.List;
import java.util.Optional;

public interface UserLessonRepository {
	boolean existsByUserIdAndLessonId(Integer userId, Integer lessonId) throws KnowyInconsistentDataException;

	List<Integer> findCourseIdsByUserId(Integer userId) throws KnowyInconsistentDataException;

	boolean existsById(int userId, int lessonId) throws KnowyInconsistentDataException;

	UserLesson save(UserLesson userLesson) throws KnowyInconsistentDataException;

	int countByUserIdAndCourseIdAndStatus(Integer userId, Integer courseId, UserLesson.ProgressStatus status) throws KnowyInconsistentDataException;

	Optional<UserLesson> findById(int userId, int lessonId) throws KnowyInconsistentDataException;

	List<UserLesson> findAllByCourseId(int userId, int courseId) throws KnowyInconsistentDataException;
}