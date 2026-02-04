package com.knowy.server.api.usecase.user;

import com.knowy.core.CourseService;
import com.knowy.core.domain.Category;
import com.knowy.core.domain.Filter;
import com.knowy.core.domain.Page;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.core.user.domain.User;
import com.knowy.core.util.KnowyUseCase;
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.dto.CourseCardDto;
import com.knowy.server.api.dto.PaginationData;
import com.knowy.server.api.dto.UserRecommendationsGet200Response;
import com.knowy.server.api.mapper.CourseCardDtoMapper;
import com.knowy.server.api.mapper.OrderMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

// JAVADOC
public class GetUserRecommendation implements KnowyUseCase<PaginationData, UserRecommendationsGet200Response> {

	private final CourseService courseService;
	private final CourseCardDtoMapper courseCardDtoMapper = new CourseCardDtoMapper();
	private final OrderMapper orderMapper = new OrderMapper();

	public GetUserRecommendation(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	@Override
	public UserRecommendationsGet200Response execute(PaginationData paginationData) {
		User user = new SecurityHelper().getAuthenticatedUser();
		Pagination pagination = createPagination(paginationData, user.categories());

		return new UserRecommendationsGet200Response()
			.results(getCourseCardDto(user, pagination));

	}

	private Pagination createPagination(PaginationData paginationData, Set<Category> category) {
		var order = Optional.of(orderMapper.toDomain(paginationData.getOrder(), paginationData.getDirection()));
		var filters = (category == null || category.isEmpty())
			? List.<Filter>of()
			: List.of(new Filter("category", Filter.Operator.IN, category));

		return new Pagination(
			new Page(paginationData.getPage(), paginationData.getSize()),
			order,
			filters);
	}

	private List<CourseCardDto> getCourseCardDto(User user, Pagination pagination) {
		try {
			return courseService.getRecommendedCourses(user.id(), user.categories()).stream()
				.limit(3)
				.map(courseCardDtoMapper::toDto)
				.toList();
		} catch (KnowyInconsistentDataException e) {
			throw new KnowyInternalServerErrorException("Failed to fetch paginated user course data", e);
		}
	}
}
