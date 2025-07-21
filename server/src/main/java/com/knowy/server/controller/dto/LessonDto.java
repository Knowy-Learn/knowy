package com.knowy.server.controller.dto;

import com.knowy.server.entity.PublicUserLessonEntity;

import java.util.List;

public record LessonDto(
        int id,
        String title,
        String image,
        String duration,
        LessonStatus status,
		Integer nextLessonId
) {

    public static List<LessonDto> fromEntities(List<PublicUserLessonEntity> userLessons) {
        return userLessons.stream()
                .map(LessonDto::fromEntity)
                .toList();
    }

    public static LessonDto fromEntity(PublicUserLessonEntity userLesson) {
		Integer nextLessonId = null;
		if (userLesson.getLessonEntity().getNextLesson() != null) {
			nextLessonId = userLesson.getLessonEntity().getNextLesson().getId();
		}
		return new LessonDto(
			userLesson.getLessonId(),
			userLesson.getLessonEntity().getTitle(),
			null,
			null,
			LessonDto.LessonStatus.fromString(userLesson.getStatus()),
			nextLessonId
		);
    }

    public enum LessonStatus {
        COMPLETE,
        NEXT_LESSON,
        BLOCKED;

        public static LessonStatus fromString(String status) {
            return switch (status.toLowerCase()) {
                case "completed" -> LessonDto.LessonStatus.COMPLETE;
                case "in_progress" -> LessonDto.LessonStatus.NEXT_LESSON;
                default -> LessonDto.LessonStatus.BLOCKED;
            };
        }
    }
}


