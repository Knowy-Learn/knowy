package com.knowy.server.api.controller;

import com.knowy.core.domain.Filter;
import com.knowy.core.domain.Order;
import com.knowy.core.domain.Page;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.core.port.UserLessonRepository;
import com.knowy.server.api.dto.*;
import com.knowy.server.api.usecase.user.GetLearnCoursesDataUseCase;
import com.knowy.server.api.usecase.user.GetNavbarUserDataUseCase;
import com.knowy.server.api.usecase.user.GetResumeUserDataUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController implements UserApi {

	private final GetNavbarUserDataUseCase getNavbarUserDataUseCase;
	private final GetResumeUserDataUseCase getResumeUserDataUseCase;
	private final GetLearnCoursesDataUseCase getLearnCoursesDataUseCase;

	public UserController(
		CourseRepository courseRepository,
		LessonRepository lessonRepository,
		UserLessonRepository userLessonRepository,
		UserCourseRepository userCourseRepository
	) {
		this.getNavbarUserDataUseCase = new GetNavbarUserDataUseCase();
		this.getResumeUserDataUseCase = new GetResumeUserDataUseCase(
			courseRepository,
			lessonRepository,
			userLessonRepository,
			userCourseRepository
		);
		this.getLearnCoursesDataUseCase = new GetLearnCoursesDataUseCase(
			courseRepository,
			lessonRepository,
			userLessonRepository,
			userCourseRepository
		);
	}


	/**
	 * GET /user/learn/courses : Get filtered courses with pagination Fetches the user&#39;s course collection. Supports
	 * pagination, filtering by category, and custom sorting.
	 *
	 * @param page      The page number to retrieve (starting from 0). (optional, default to 0)
	 * @param size      The size of element to retrieve (optional, default to 0)
	 * @param order     Sort order for the courses (e.g., &#39;alphabetic&#39;, &#39;progress&#39;, &#39;date&#39;).
	 *                  (optional)
	 * @param direction Sort order direction for the courses. (optional)
	 * @param category  Filter courses by category language. (optional)
	 * @return A paginated list of courses. (status code 200) or Bad Request. The request is invalid or cannot be
	 * processed. (status code 400) or Access unauthorized. The request requires valid authentication credentials (e.g.,
	 * a valid token). (status code 401) or Internal Server Error. Something went wrong on the server. (status code
	 * 500)
	 */
	@Override
	public ResponseEntity<UserLearnCoursesGet200Response> userLearnCoursesGet(
		Integer page,
		Integer size,
		OrderEnum order,
		DirectionEnum direction,
		String category
	) {
		var pagination = new Pagination(
			new Page(page, size),
			Optional.of(getPaginationOrder(order, direction)),
			getPaginationFilters(category)
		);

		return ResponseEntity.ok(getLearnCoursesDataUseCase.execute(pagination));
	}

	private Order getPaginationOrder(OrderEnum orderEnum, DirectionEnum directionEnum) {
		Order.SortDirection sortDirection = Optional.ofNullable(directionEnum)
			.map(dir -> Order.SortDirection.fromString(dir.toString()))
			.orElse(Order.SortDirection.ASCENDING);

		return switch (orderEnum != null ? orderEnum : OrderEnum.ALPHABETIC) {
			case CREATED_AT -> new Order("creationDate", sortDirection);
			case AUTHOR -> new Order("author", sortDirection);
			default -> new Order("title", sortDirection);
		};
	}

	private List<Filter> getPaginationFilters(String category) {
		return (category != null && !category.isBlank())
			? List.of(new Filter("category", Filter.Operator.EQUALS, category))
			: List.of();
	}

	/**
	 * GET /user/navbar : Get navbar user data Retrieve the data needed to display the user&#39;s navbar, such as name,
	 * avatar, and notifications.
	 *
	 * @return Navbar data retrieved successfully (status code 200) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401)
	 */
	@Override
	public ResponseEntity<UserNavbarGet200Response> userNavbarGet() {
		return ResponseEntity.ok(getNavbarUserDataUseCase.execute());
	}

	/**
	 * GET /user/recommendations : Get course recommendations for the current user Returns a personalized list of
	 * courses based on the user&#39;s interests, past enrollments, and browsing history.
	 *
	 * @return A list of recommended courses (status code 200) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401) or Internal Server Error. Something went
	 * wrong on the server. (status code 500)
	 */
	@Override
	public ResponseEntity<UserRecommendationsGet200Response> userRecommendationsGet() {
		return null; //TODO
	}

	/**
	 * GET /user/resume : Get resume user data Retrieve a summary of the user&#39;s profile, including basic information
	 * and course progress.
	 *
	 * @return Resume data retrieved successfully (status code 200) or Access unauthorized. The request requires valid
	 * authentication credentials (e.g., a valid token). (status code 401)
	 */
	@Override
	public ResponseEntity<UserResumeGet200Response> userResumeGet() {
		try {
			return ResponseEntity.ok(getResumeUserDataUseCase.execute());
		} catch (KnowyInconsistentDataException e) {
			throw new RuntimeException(e); // TODO
		}
	}
}
