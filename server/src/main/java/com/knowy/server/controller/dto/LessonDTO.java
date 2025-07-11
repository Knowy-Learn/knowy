package com.knowy.server.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LessonDTO {
	private int number;
	private String title;
	private String image;
	private String duration;
	private LessonStatus status; //depende de ENUM LessonStatus

	public enum LessonStatus {
		COMPLETE,
		NEXT_LESSON,
		BLOCKED
	}
}


