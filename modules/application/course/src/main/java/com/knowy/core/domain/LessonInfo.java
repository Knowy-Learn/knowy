package com.knowy.core.domain;

public interface LessonInfo {
    int id();

    int courseId();

    Integer nextLessonId();

    String title();

    String explanation();
}
