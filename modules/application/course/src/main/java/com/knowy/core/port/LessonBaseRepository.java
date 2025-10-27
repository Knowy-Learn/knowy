package com.knowy.core.port;

import com.knowy.core.domain.LessonInfo;
import com.knowy.core.exception.KnowyDataAccessException;

import java.util.Optional;

public interface LessonBaseRepository {
	Optional<LessonInfo> findById(int lessonId) throws KnowyDataAccessException;
}
