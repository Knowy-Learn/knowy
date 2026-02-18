package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.*;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserCourseMapper;
import com.knowy.persistence.adapter.spring.mapper.SpringPaginationMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.knowy.core.util.CommonUtils.nonEmptyElse;

public class JpaUserCourseRepository implements UserCourseRepository {

	private final JpaCourseDao jpaCourseDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaCategoryDao jpaCategoryDao;
	private final JpaUserDao jpaUserDao;
	private final JpaLessonDao jpaLessonDao;
	private final JpaExerciseDao jpaExerciseDao;

	public JpaUserCourseRepository(
		JpaCourseDao jpaCourseDao,
		JpaUserLessonDao jpaUserLessonDao,
		JpaCategoryDao jpaCategoryDao,
		JpaUserDao jpaUserDao,
		JpaLessonDao jpaLessonDao,
		JpaExerciseDao jpaExerciseDao
	) {
		this.jpaCourseDao = jpaCourseDao;
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaCategoryDao = jpaCategoryDao;
		this.jpaUserDao = jpaUserDao;
		this.jpaLessonDao = jpaLessonDao;
		this.jpaExerciseDao = jpaExerciseDao;
	}

	/**
	 * Retrieves user course details by user and course identifiers.
	 *
	 * @param userId   the unique identifier of the user
	 * @param courseId the unique identifier of the course
	 * @return an Optional containing the mapped UserCourse, or empty if no lessons found
	 * @throws KnowyDataAccessException if an error occurs during database communication
	 */
	@Override
	public Optional<UserCourse> findById(int userId, int courseId) throws KnowyDataAccessException {
		var userCourseMapper = new JpaUserCourseMapper(
			jpaUserDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao, jpaUserLessonDao, jpaCategoryDao
		);

		try {
			List<PublicUserLessonEntity> userLessonEntities = jpaUserLessonDao.findAllByUserIdAndCourseId(userId, courseId);
			if (userLessonEntities.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(userCourseMapper.toUserCourse(userId, userLessonEntities));

		} catch (DataAccessException ex) {
			throw new KnowyDataAccessException("Data access error while fetching user course details", ex);
		}
	}

	/**
	 * Retrieves a paginated list of all courses associated with a specific user.
	 *
	 * @param userId     the unique identifier of the user
	 * @param pagination the pagination configuration
	 * @return a {@link PagedResult} containing the list of {@link UserCourse} records
	 * @throws KnowyDataAccessException if there is an error accessing to the data or processing the paginated request
	 */
	@Override
	public PagedResult<UserCourse> findAllByUserId(int userId, Set<CourseStatus> courseStatuses, Pagination pagination) throws KnowyDataAccessException {
		var userCourseMapper = new JpaUserCourseMapper(
			jpaUserDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao, jpaUserLessonDao, jpaCategoryDao
		);
		Pageable pageable = new SpringPaginationMapper().toPageable(pagination);

		try {
			Set<String> categoryNames = extractCategoryNames(pagination.filters());
			Page<CourseEntity> courseEntitiesPage = jpaCourseDao.findAllByUserId(
				userId, extractStatusIds(courseStatuses), nonEmptyElse(categoryNames, null), pageable
			);

			return new PagedResult<>(
				pagination.page(),
				userCourseMapper.toUserCourses(userId, courseEntitiesPage.getContent()),
				courseEntitiesPage.getTotalElements()
			);
		} catch (DataAccessException e) {
			throw new KnowyDataAccessException("Error while try to fetch user courses", e);
		}
	}

	private Set<Integer> extractStatusIds(Set<CourseStatus> courseStatuses) {
		return courseStatuses.stream()
			.map(CourseStatus::ordinal)
			.collect(Collectors.toSet());
	}

	private Set<String> extractCategoryNames(Set<Filter> filters) {
		return filters.stream()
			.filter(filter -> "category".equals(filter.fieldName()))
			.flatMap(filter -> filter.value() instanceof Set<?> s ? s.stream() : Stream.empty())
			.filter(CategoryUnidentifiedData.InmutableCategoryUnidentifiedData.class::isInstance)
			.map(CategoryUnidentifiedData.InmutableCategoryUnidentifiedData.class::cast)
			.map(CategoryUnidentifiedData.InmutableCategoryUnidentifiedData::name)
			.collect(Collectors.toSet());
	}
}
