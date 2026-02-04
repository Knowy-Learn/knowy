package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.domain.*;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.dto.*;
import com.knowy.server.api.mapper.CourseCardDtoMapper;
import com.knowy.server.api.mapper.CourseStatusEnumMapper;
import com.knowy.server.api.mapper.OrderMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// JAVADOC
public class GetLearnCoursesDataUseCase {

	private final CourseService courseService;
	private final CourseCardDtoMapper courseMapper = new CourseCardDtoMapper();
	private final CourseStatusEnumMapper statusMapper = new CourseStatusEnumMapper();
	private final OrderMapper orderMapper = new OrderMapper();

	public GetLearnCoursesDataUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	public UserLearnCoursesGet200Response execute(
		PaginationData paging,
		Set<CourseStatusEnum> coursesStatuses,
		String category
	) {
		User user = new SecurityHelper().getAuthenticatedUser();
		Pagination paginationRequest = createPagination(paging, category);

		try {
			PagedResult<UserCourse> pagedResult = courseService.getAllUserCoursesByUserId(
				user.id(),
				statusMapper.toDomain(coursesStatuses),
				paginationRequest
			);

			return new UserLearnCoursesGet200Response()
				.info(extractMetadata(pagedResult))
				.results(mapToDtoList(pagedResult.collection()));

		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorException("Failed to fetch paginated user course data", e);
		}
	}

	private Pagination createPagination(PaginationData paging, String category) {
		var order = Optional.of(orderMapper.toDomain(paging.getOrder(), paging.getDirection()));
		var filters = (category == null || category.isBlank())
			? List.<Filter>of()
			: List.of(new Filter("category", Filter.Operator.EQUALS, category));

		return new Pagination(
			new Page(paging.getPage(), paging.getSize()),
			order,
			filters);
	}

	private PaginationMetadata extractMetadata(PagedResult<UserCourse> result) {
		return new PaginationMetadata()
			.total(result.totalItems())
			.pages(result.pages())
			.size(result.page().size())
			.page(result.page().number());
	}

	private List<CourseCardDto> mapToDtoList(Collection<UserCourse> collection) {
		return collection.stream().map(courseMapper::toDto).toList();
	}
}
