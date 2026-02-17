package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.domain.*;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorRuntimeException;
import com.knowy.server.api.dto.CourseCardDto;
import com.knowy.server.api.dto.PaginatedCourseResponseWrapper;
import com.knowy.server.api.dto.PaginationData;
import com.knowy.server.api.dto.PaginationMetadata;
import com.knowy.server.api.mapper.CourseMapper;
import com.knowy.server.api.mapper.OrderMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FindNotSubscribedCoursesUseCase implements KnowyUseCase<FindNotSubscribedCoursesCommand, PaginatedCourseResponseWrapper> {

	private final CourseService courseService;
	private final CourseMapper courseMapper = new CourseMapper();
	private final OrderMapper orderMapper = new OrderMapper();

	/**
	 * Initializes the use case with the required infrastructure repositories.
	 *
	 * @param courseRepository     the repository for general course data.
	 * @param lessonRepository     the repository for lesson details.
	 * @param userLessonRepository the repository for tracking user progress in lessons.
	 * @param userCourseRepository the repository for user-specific course associations.
	 */
	public FindNotSubscribedCoursesUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	@Override
	public PaginatedCourseResponseWrapper execute(FindNotSubscribedCoursesCommand param) {
		User user = new SecurityHelper().getAuthenticatedUser();
		Pagination paginationRequest = createPagination(param.paging(), param.categories());

		try {
			PagedResult<Course> pagedResult = courseService.findNotSubscribed(user.id(), paginationRequest);

			return new PaginatedCourseResponseWrapper()
				.info(extractMetadata(pagedResult))
				.results(mapToDtoList(pagedResult.collection()));

		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorRuntimeException("Failed to fetch paginated user course data", e);
		}
	}

	private Pagination createPagination(PaginationData paging, List<String> categories) {
		var order = Optional.of(orderMapper.toDomain(paging.getOrder(), paging.getDirection()));
		var filters = (categories == null || categories.isEmpty())
			? Set.<Filter>of()
			: Set.of(new Filter("category", Filter.Operator.EQUALS, mapToCategory(categories)));

		return new Pagination(
			new Page(paging.getPage(), paging.getSize()),
			order,
			filters);
	}

	private Set<CategoryUnidentifiedData.InmutableCategoryUnidentifiedData> mapToCategory(List<String> category) {
		return category.stream()
			.map(CategoryUnidentifiedData.InmutableCategoryUnidentifiedData::new)
			.collect(Collectors.toSet());
	}

	private PaginationMetadata extractMetadata(PagedResult<Course> result) {
		return new PaginationMetadata()
			.total(result.totalItems())
			.pages(result.pages())
			.size(result.page().size())
			.page(result.page().number());
	}

	private List<CourseCardDto> mapToDtoList(Collection<Course> collection) {
		return collection.stream().map(courseMapper::toCourseCardDto).toList();
	}
}

