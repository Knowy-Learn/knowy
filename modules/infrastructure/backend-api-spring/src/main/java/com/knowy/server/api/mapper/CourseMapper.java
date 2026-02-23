package com.knowy.server.api.mapper;

import com.knowy.core.domain.Course;
import com.knowy.core.domain.UserCourse;
import com.knowy.server.api.dto.CourseCardDto;
import com.knowy.server.api.dto.CourseDto;

import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper for converting UserCourse domain objects into CourseCardDto objects.
 */
public class CourseMapper {

	/**
	 * Maps a UserCourse to a CourseCardDto including nested image and category data.
	 *
	 * @param userCourse the domain entity to map.
	 * @return the mapped CourseCardDto.
	 * @throws NullPointerException if userCourse is null.
	 */
	public CourseCardDto toCourseCardDto(UserCourse userCourse) {
		Objects.requireNonNull(userCourse);

		var imageDtoMapper = new ImageMapper();
		var categoryDtoMapper = new CategoryMapper();

		var info = userCourse.courseInfo();
		return new CourseCardDto(
			info.id(),
			info.title(),
			info.description(),
			imageDtoMapper.toImageDto(info.image()),
			info.author(),
			info.creationDate().atOffset(ZoneOffset.UTC),
			categoryDtoMapper.toCategoryDto(info.categories()),
			(float) userCourse.courseProgress()
		);
	}

	/**
	 * Maps a list of Course entities to a list of CourseCardDto objects.
	 *
	 * @param courses the list of domain entities to map.
	 * @return a list of mapped CourseCardDto objects.
	 */
	public List<CourseCardDto> toCourseCardDto(List<Course> courses) {
		return courses.stream()
			.map(this::toCourseCardDto)
			.toList();
	}

	/**
	 * Maps a Course domain entity to a CourseCardDto. Sets a default progress of 0.0F as it is not a user-specific
	 * record.
	 *
	 * @param course the domain entity to map.
	 * @return the mapped CourseCardDto.
	 * @throws NullPointerException if course is null.
	 */
	public CourseCardDto toCourseCardDto(Course course) {
		Objects.requireNonNull(course);

		var imageDtoMapper = new ImageMapper();
		var categoryDtoMapper = new CategoryMapper();

		return new CourseCardDto(
			course.id(),
			course.title(),
			course.description(),
			imageDtoMapper.toImageDto(course.image()),
			course.author(),
			course.creationDate().atOffset(ZoneOffset.UTC),
			categoryDtoMapper.toCategoryDto(course.categories()),
			0.0F
		);
	}

	public CourseDto toCourseDto(UserCourse userCourse) {
		Objects.requireNonNull(userCourse);

		var categoryMapper = new CategoryMapper();
		var userLessonMapper = new LessonMapper();


		return new CourseDto(
			userCourse.courseInfo().id(),
			userCourse.courseInfo().title(),
			userCourse.courseInfo().description(),
			userCourse.courseInfo().author(),
			userCourse.courseInfo().creationDate().atOffset(ZoneOffset.UTC),
			userCourse.courseInfo().categories().stream()
				.map(categoryMapper::toCategoryDto)
				.collect(Collectors.toSet()),
			userCourse.userLessons().stream()
				.map(userLessonMapper::toLessonStepDto)
				.collect(Collectors.toCollection(LinkedHashSet::new))
		);
	}
}
