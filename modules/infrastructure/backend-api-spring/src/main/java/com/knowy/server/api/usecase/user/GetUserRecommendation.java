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
import com.knowy.server.api.controller.exception.KnowyInternalServerErrorException;
import com.knowy.server.api.dto.PaginatedCourseResponseWrapper;
import com.knowy.server.api.dto.PaginationData;
import com.knowy.server.api.mapper.CourseMapper;
import com.knowy.server.api.mapper.OrderMapper;
import com.knowy.server.api.mapper.PagedResultMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

// JAVADOC
public class GetUserRecommendation implements KnowyUseCase<PaginationData, PaginatedCourseResponseWrapper> {

	private final CourseService courseService;
	private final CourseMapper courseMapper = new CourseMapper();
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
	public PaginatedCourseResponseWrapper execute(PaginationData paginationData) {
		User user = new SecurityHelper().getAuthenticatedUser();
		Pagination pagination = createPagination(paginationData, user.categories());
		PagedResult<Course> coursesPaged = getCourses(user, pagination);

		return new PaginatedCourseResponseWrapper()
			.info(new PagedResultMapper().toPaginationMetaData(coursesPaged))
			.results(new CourseMapper().toCourseCardDto((List<Course>) coursesPaged.collection()));

	}

	private Pagination createPagination(PaginationData paginationData, Set<Category> category) {
		var order = Optional.of(orderMapper.toDomain(paginationData.getOrder(), paginationData.getDirection()));
		var filters = (category == null || category.isEmpty())
			? Set.<Filter>of()
			: Set.of(new Filter("category", Filter.Operator.IN, category));

		return new Pagination(
			new Page(paginationData.getPage(), paginationData.getSize()),
			order,
			filters);
	}

	private PagedResult<Course> getCourses(User user, Pagination pagination) {
		try {
			return courseService.getRecommendedCourses(user.id(), pagination);
		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorException("Failed to fetch paginated user course data", e);
		}
	}
}
