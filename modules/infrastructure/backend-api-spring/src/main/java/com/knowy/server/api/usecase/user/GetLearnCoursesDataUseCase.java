package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.domain.PagedResult;
import com.knowy.core.domain.Pagination;
import com.knowy.core.domain.UserCourse;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.dto.CourseCardDto;
import com.knowy.server.api.dto.PaginationMetadata;
import com.knowy.server.api.dto.UserLearnCoursesGet200Response;
import com.knowy.server.api.mapper.CategoryDtoMapper;
import com.knowy.server.api.mapper.ImageDtoMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.time.ZoneOffset;
import java.util.List;

// JAVADOC
public class GetLearnCoursesDataUseCase {

	private final CourseService courseService;

	public GetLearnCoursesDataUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	public UserLearnCoursesGet200Response execute(Pagination pagination) {
		User user = new SecurityHelper().getAuthenticatedUser();

		try {
			PagedResult<UserCourse> pagedResult = courseService.getAllUserCoursesByUserId(user.id(), pagination);

			var paginationMetadata = new PaginationMetadata()
				.total(pagedResult.totalItems())
				.pages(pagedResult.pages())
				.size(pagedResult.page().size())
				.page(pagedResult.page().number());

			List<CourseCardDto> courseCardDtos = pagedResult.collection().stream()
				.map(this::userCourseToCourseCardDto)
				.toList();

			return new UserLearnCoursesGet200Response()
				.info(paginationMetadata)
				.results(courseCardDtos);
		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorException("", e);
		}
	}

	private CourseCardDto userCourseToCourseCardDto(UserCourse userCourse) {
		return new CourseCardDto(
			userCourse.courseInfo().id(),
			userCourse.courseInfo().title(),
			userCourse.courseInfo().description(),
			new ImageDtoMapper().toDto(userCourse.courseInfo().image()),
			userCourse.courseInfo().author(),
			userCourse.courseInfo().creationDate().atOffset(ZoneOffset.UTC),
			new CategoryDtoMapper().categoriesToDto(userCourse.courseInfo().categories()),
			(float) userCourse.courseProgress()
		);
	}
}
