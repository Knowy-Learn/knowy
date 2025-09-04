package com.knowy.core.domain;

import java.util.Objects;

public class LessonBase implements LessonInfo {

	private final int id;
	private final int courseId;
	private final Integer nextLessonId;
	private final String title;
	private final String explanation;

	public LessonBase(int id, int courseId, Integer nextLessonId, String title, String explanation) {
		this.id = id;
		this.courseId = courseId;
		this.nextLessonId = nextLessonId;
		this.title = title;
		this.explanation = explanation;
	}

	public int id() {
		return id;
	}

	public int courseId() {
		return courseId;
	}

	public Integer nextLessonId() {
		return nextLessonId;
	}

	public String title() {
		return title;
	}

	public String explanation() {
		return explanation;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) return false;
		LessonBase that = (LessonBase) object;
		return id == that.id &&
			courseId == that.courseId &&
			Objects.equals(nextLessonId, that.nextLessonId) &&
			Objects.equals(title, that.title) &&
			Objects.equals(explanation, that.explanation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, courseId, nextLessonId, title, explanation);
	}
}
