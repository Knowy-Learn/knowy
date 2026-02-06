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
import com.knowy.server.api.mapper.CourseMapper;
import com.knowy.server.api.mapper.CourseStatusEnumMapper;
import com.knowy.server.api.mapper.OrderMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Use case for retrieving paginated course data for the authenticated user's learning section. It handles filtering by
 * status and category, pagination, and mapping results to DTOs.
 */
public class GetLearnCoursesDataUseCase {

	private final CourseService courseService;
	private final CourseMapper courseMapper = new CourseMapper();
	private final CourseStatusEnumMapper statusMapper = new CourseStatusEnumMapper();
	private final OrderMapper orderMapper = new OrderMapper();

	/**
	 * Initializes the use case with the required infrastructure repositories.
	 *
	 * @param courseRepository     the repository for general course data.
	 * @param lessonRepository     the repository for lesson details.
	 * @param userLessonRepository the repository for tracking user progress in lessons.
	 * @param userCourseRepository the repository for user-specific course associations.
	 */
	public GetLearnCoursesDataUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	/**
	 * Executes the process of fetching and mapping user courses based on the provided filters and pagination.
	 *
	 * @param paging          the pagination and sorting criteria.
	 * @param coursesStatuses the set of statuses to filter the courses.
	 * @param category        an optional category filter.
	 * @return a response object containing paginated course DTOs and metadata.
	 * @throws KnowyInternalServerErrorException if a data access error occurs during execution.
	 */
	public PaginatedCourseResponseWrapper execute(
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

			return new PaginatedCourseResponseWrapper()
				.info(extractMetadata(pagedResult))
				.results(mapToDtoList(pagedResult.collection()));

		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorException("Failed to fetch paginated user course data", e);
		}
	}

	private Pagination createPagination(PaginationData paging, String category) {
		var order = Optional.of(orderMapper.toDomain(paging.getOrder(), paging.getDirection()));
		var filters = (category == null || category.isBlank())
			? Set.<Filter>of()
			: Set.of(new Filter("category", Filter.Operator.EQUALS, category));

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
		return collection.stream().map(courseMapper::toCourseCardDto).toList();
	}
}
