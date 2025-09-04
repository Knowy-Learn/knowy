package com.knowy.core.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Lesson extends LessonBase implements LessonInfo, LessonContent {
	private final Set<Documentation> documentations;
	private final Set<Exercise> exercises;

	public Lesson(
		int id,
		int courseId,
		Integer nextLessonId,
		String title,
		String explanation,
		Set<Documentation> documentations,
		Set<Exercise> exercises
	) {
		super(id, courseId, nextLessonId, title, explanation);
		this.documentations = documentations;
		this.exercises = exercises;
	}

	public Lesson(
		int id,
		int courseId,
		Integer nextLessonId,
		String title,
		String explanation
	) {
		this(id, courseId, nextLessonId, title, explanation, new HashSet<>(), new HashSet<>());
	}

	public Set<Documentation> documentations() {
		return Collections.unmodifiableSet(documentations);
	}

	public Set<Exercise> exercises() {
		return Collections.unmodifiableSet(exercises);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Lesson) obj;
		return this.id() == that.id() &&
			this.courseId() == that.courseId() &&
			Objects.equals(this.nextLessonId(), that.nextLessonId()) &&
			Objects.equals(this.title(), that.title()) &&
			Objects.equals(this.explanation(), that.explanation()) &&
			Objects.equals(this.documentations(), that.documentations()) &&
			Objects.equals(this.exercises(), that.exercises());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id(), courseId(), nextLessonId(), title(), explanation(), documentations, exercises);
	}

	@Override
	public String toString() {
		return "Lesson[" +
			"id=" + id() + ", " +
			"courseId=" + courseId() + ", " +
			"nextLessonId=" + nextLessonId() + ", " +
			"title=" + title() + ", " +
			"explanation=" + explanation() + ", " +
			"documentations=" + documentations() + ", " +
			"exercises=" + exercises() + ']';
	}
}