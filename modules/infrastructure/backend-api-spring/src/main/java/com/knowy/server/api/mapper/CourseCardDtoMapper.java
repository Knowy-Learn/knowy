package com.knowy.server.api.mapper;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.UserCourse;
import com.knowy.server.api.dto.CourseCardDto;

import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Mapper for converting UserCourse domain objects into CourseCardDto objects.
 */
public class CourseCardDtoMapper {

	/**
	 * Maps a UserCourse to a CourseCardDto including nested image and category data.
	 *
	 * @param userCourse the domain entity to map.
	 * @return the mapped CourseCardDto.
	 * @throws NullPointerException if userCourse is null.
	 */
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

	// JAVADOC
	public CourseCardDto toDto(Course course) {
		Objects.requireNonNull(course);

		var imageDtoMapper = new ImageDtoMapper();
		var categoryDtoMapper = new CategoryDtoMapper();

		return new CourseCardDto(
			course.id(),
			course.title(),
			course.description(),
			imageDtoMapper.toDto(course.image()),
			course.author(),
			course.creationDate().atOffset(ZoneOffset.UTC),
			categoryDtoMapper.toDto(course.categories()),
			0.0F
		);
	}
}
