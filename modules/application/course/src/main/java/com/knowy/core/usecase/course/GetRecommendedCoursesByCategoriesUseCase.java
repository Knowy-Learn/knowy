package com.knowy.core.usecase.course;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Use case for retrieving a recommended list of courses based on a set of categories.
 */
public class GetRecommendedCoursesByCategoriesUseCase {

	private final CourseRepository courseRepository;

	/**
	 * Constructs the use case with the given course repository.
	 *
	 * @param courseRepository the repository used to fetch course data
	 */
	public GetRecommendedCoursesByCategoriesUseCase(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	/**
	 * Retrieves a list of recommended courses for a user based on the provided categories.
	 *
	 * <p>This method fetches courses that the user is not already subscribed to, sorts them
	 * by relevance to the given categories, and returns the top 3 recommended courses.</p>
	 *
	 * @param userId     the ID of the user for whom the recommendations are generated
	 * @param categories a set of {@link Category} entities to guide the course recommendations
	 * @return a list of {@link Course} entities recommended for the user
	 * @throws KnowyInconsistentDataException if inconsistencies occur when retrieving course data
	 */
	public List<Course> execute(int userId, Set<Category> categories) throws KnowyInconsistentDataException {
		Set<Course> coursesWhereUserIsSubscribed = courseRepository.findAllWhereUserIsSubscribed(userId);
		Stream<Course> candidateCoursesStream = Stream.concat(
			courseRepository.findByCategoriesStreamingInRandomOrder(categories),
			courseRepository.findAllStreamingInRandomOrder()
		);

		return candidateCoursesStream
			.distinct()
			.filter(Predicate.not(coursesWhereUserIsSubscribed::contains))
			.limit(3)
			.toList();
	}

}
