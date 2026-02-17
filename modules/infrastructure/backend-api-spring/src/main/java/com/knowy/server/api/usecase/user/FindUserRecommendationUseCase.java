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
import com.knowy.server.api.dto.PaginatedCourseResponseWrapper;
import com.knowy.server.api.dto.PaginationData;
import com.knowy.server.api.mapper.CourseMapper;
import com.knowy.server.api.mapper.PagedResultMapper;
import com.knowy.server.api.util.SecurityHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Use case for retrieving a paginated list of recommended courses for the authenticated user. Recommendations are
 * generated based on the user's preferred categories and requested pagination criteria (sorting, page size, etc.).
 */
public class FindUserRecommendationUseCase implements KnowyUseCase<PaginationData, PaginatedCourseResponseWrapper> {

	private final CourseService courseService;

	/**
	 * Constructs the use case and initializes the internal CourseService.
	 *
	 * @param courseRepository     the repository for course data.
	 * @param lessonRepository     the repository for lesson data.
	 * @param userLessonRepository the repository for user-specific lesson progress.
	 * @param userCourseRepository the repository for user-specific course enrollment.
	 */
	public FindUserRecommendationUseCase(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.courseService = new CourseService(courseRepository, lessonRepository, userLessonRepository, userCourseRepository);
	}

	/**
	 * Executes the recommendation logic for the currently authenticated user.
	 *
	 * @param paginationData the pagination, sorting, and ordering parameters.
	 * @return a wrapper containing the list of recommended courses and pagination metadata.
	 * @throws KnowyInternalServerErrorRuntimeException if there is an error during data retrieval.
	 */
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
		var filters = (category == null || category.isEmpty())
			? Set.<Filter>of()
			: Set.of(new Filter("category", Filter.Operator.IN, category));

		return new Pagination(
			new Page(paginationData.getPage(), paginationData.getSize()),
			Optional.empty(),
			filters);
	}

	private PagedResult<Course> getCourses(User user, Pagination pagination) {
		try {
			return courseService.findRecommended(user.id(), pagination);
		} catch (KnowyDataAccessException e) {
			throw new KnowyInternalServerErrorRuntimeException("Failed to fetch paginated user course data", e);
		}
	}
}
