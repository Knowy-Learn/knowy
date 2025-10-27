package com.knowy.core.domain;

public record LessonInfo(
        int id,
        int courseId,
        Integer nextLessonId,
        String title,
        String explanation
) implements LessonIdentifier, LessonMinData {
}
