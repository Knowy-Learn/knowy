package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.*;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.port.UserCourseRepository;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaCourseInfoMapper;
import com.knowy.persistence.adapter.jpa.mapper.JpaUserLessonMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
	 * Retrieves a specific course enrollment record for a user.
	 *
	 * @param userId   the unique identifier of the user
	 * @param courseId the unique identifier of the course
	 * @return the {@link UserCourse} record associated with the user and course
	 * @throws KnowyDataAccessException if there is an error accessing to the data
	 */
	@Override
	public UserCourse findById(int userId, int courseId) throws KnowyDataAccessException {
		var userLessonMapper = new JpaUserLessonMapper(jpaUserDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao);
		var courseInfoMapper = new JpaCourseInfoMapper(jpaCategoryDao);

		List<PublicUserLessonEntity> userLessonEntities = jpaUserLessonDao.findAllByUserIdAndCourseId(userId, courseId);

		CourseIdentifiedInfo courseIdentifiedInfo = courseInfoMapper.toDomain(userLessonEntities.getFirst().getLessonEntity().getCourse());
		List<UserLesson> userLessons = userLessonEntities.stream()
			.map(userLessonMapper::toDomain)
			.toList();

		return new UserCourse(userId, courseIdentifiedInfo, userLessons);
	}

	/**
	 * Retrieves a paginated list of all courses associated with a specific user.
	 *
	 * @param userId     the unique identifier of the user
	 * @param pagination the pagination configuration
	 * @return a {@link PagedResult} containing the list of {@link UserCourse} records
	 * @throws KnowyDataAccessException if there is an error accessing to the data or processing the paginated request
	 */
	// TODO: Implement Filters correctly
	@Override
	public PagedResult<UserCourse> findAllByUserId(int userId, Set<CourseStatus> coursesStatusIds, Pagination pagination) throws KnowyDataAccessException {
		Pageable pageable = PageRequest.of(
			pagination.page().number(),
			pagination.page().size(),
			pagination.order().map(this::toSpringSort)
				.orElse(Sort.unsorted())
		);

		Page<CourseEntity> courseEntitiesPage = jpaCourseDao.findAllByUserId(
			userId,
			coursesStatusIds.stream().map(this::statusToInt).collect(Collectors.toSet()),
			pageable
		);
		List<UserCourse> userCourses = toUserCourses(userId, courseEntitiesPage.getContent());

		return new PagedResult<>(pagination.page(), userCourses, courseEntitiesPage.getTotalElements());
	}

	private Sort toSpringSort(Order order) {
		Sort.Direction direction = order.direction() == Order.SortDirection.ASCENDING
			? Sort.Direction.ASC
			: Sort.Direction.DESC;

		return Sort.by(direction, order.field());
	}

	private int statusToInt(CourseStatus courseStatus) {
		return switch (courseStatus) {
			case NOT_STARTED -> 0;
			case COMPLETED -> 1;
			case IN_PROGRESS -> 2;
		};
	}

	private List<UserCourse> toUserCourses(int userId, List<CourseEntity> courseEntities) {
		List<Integer> coursesId = getCoursesId(courseEntities);
		Map<Integer, List<JpaUserLessonDao.UserLessonCourseInfo>> userLessonCourseInfos = jpaUserLessonDao
			.findAllWithCourseInfoByCoursesId(coursesId).stream()
			.collect(Collectors.groupingBy(
				userLessonCourseInfo -> userLessonCourseInfo.courseEntity().getId())
			);

		return courseEntities.stream()
			.map(mappingToUserCourse(userId, userLessonCourseInfos))
			.toList();
	}

	private Function<CourseEntity, UserCourse> mappingToUserCourse(
		int userId,
		Map<Integer, List<JpaUserLessonDao.UserLessonCourseInfo>> userLessonCourseInfos
	) {
		var userLessonMapper = new JpaUserLessonMapper(jpaUserDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao);
		var courseInfoMapper = new JpaCourseInfoMapper(jpaCategoryDao);

		return courseEntity -> new UserCourse(
			userId,
			courseInfoMapper.toDomain(courseEntity),
			userLessonCourseInfos.get(courseEntity.getId()).stream()
				.map(JpaUserLessonDao.UserLessonCourseInfo::userLessonEntity)
				.map(userLessonMapper::toDomain)
				.toList()
		);
	}

	private List<Integer> getCoursesId(List<CourseEntity> courseEntities) {
		return courseEntities.stream()
			.map(CourseEntity::getId)
			.toList();
	}
}
