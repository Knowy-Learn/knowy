package com.knowy.core.port;


import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyInconsistentDataException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserLessonRepository {

	List<Integer> findCourseIdsByUserId(Integer userId) throws KnowyInconsistentDataException;

	Optional<UserLesson> findById(int userId, int lessonId) throws KnowyInconsistentDataException;

	List<UserLesson> findAllByUserIdAndCourseId(int userId, int courseId) throws KnowyInconsistentDataException;

	List<UserLesson> findAllWhereUserIsSubscribed(int userId) throws KnowyInconsistentDataException;

	UserLesson save(UserLesson userLesson) throws KnowyInconsistentDataException;

	List<UserLesson> saveAll(Collection<UserLesson> userLessons) throws KnowyInconsistentDataException;
}