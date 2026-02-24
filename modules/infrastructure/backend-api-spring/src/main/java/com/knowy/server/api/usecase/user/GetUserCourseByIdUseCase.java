package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.server.api.controller.exception.KnowyBadRequestRuntimeException;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorRuntimeException;
import com.knowy.server.api.dto.UserCourseDto;
import com.knowy.server.api.mapper.CourseMapper;
import com.knowy.server.api.util.SecurityHelper;

public class GetUserCourseByIdUseCase implements KnowyUseCase<Integer, UserCourseDto> {

	private final CourseService courseService;
	private final CourseMapper courseMapper = new CourseMapper();

	public GetUserCourseByIdUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	@Override
	public UserCourseDto execute(Integer courseId) {
		if (courseId == null) throw new KnowyBadRequestRuntimeException("Course ID cannot be null.");

		try {
			User user = new SecurityHelper().getAuthenticatedUser();

			return courseService.findUserCourseById(user.id(), courseId)
				.map(courseMapper::toUserCourseDto)
				.orElseThrow(() -> new KnowyBadRequestRuntimeException("Course not found or user is not enrolled."));
		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorRuntimeException(
				"An unexpected error occurred while retrieving course information.", e
			);
		}
	}
}
