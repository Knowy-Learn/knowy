package com.knowy.server.api.mapper;

import com.knowy.core.domain.UserCourse;
import com.knowy.server.api.dto.CourseCardDto;

import java.time.ZoneOffset;
import java.util.Objects;

public class CourseCardDtoMapper {

	public CourseCardDto toDto(UserCourse userCourse) {
		Objects.requireNonNull(userCourse);

		var imageDtoMapper = new ImageDtoMapper();
		var categoryDtoMapper = new CategoryDtoMapper();

		var info = userCourse.courseInfo();
		return new CourseCardDto(
			info.id(),
			info.title(),
			info.description(),
			imageDtoMapper.toDto(info.image()),
			info.author(),
			info.creationDate().atOffset(ZoneOffset.UTC),
			categoryDtoMapper.toDto(info.categories()),
			(float) userCourse.courseProgress()
		);
	}
}
