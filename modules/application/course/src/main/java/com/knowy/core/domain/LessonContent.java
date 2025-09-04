package com.knowy.core.domain;

import java.util.Set;

public interface LessonContent {
	int id();

	Set<Documentation> documentations();

	Set<Exercise> exercises();
}
