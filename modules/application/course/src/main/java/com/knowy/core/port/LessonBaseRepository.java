package com.knowy.core.port;

import com.knowy.core.domain.LessonBase;
import com.knowy.core.exception.KnowyDataAccessException;

import java.util.Optional;

public interface LessonBaseRepository {
	Optional<LessonBase> findById(int lessonId) throws KnowyDataAccessException;
}
