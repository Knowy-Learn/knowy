package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.UserCourse;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mapper class to transform JPA entities and DAO data into UserCourse domain objects.
 */
public class JpaUserCourseMapper {

	private final JpaUserDao jpaUserDao;
	private final JpaLessonDao jpaLessonDao;
	private final JpaCourseDao jpaCourseDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaCategoryDao jpaCategoryDao;

	/**
	 * Initializes the mapper with the required DAOs for data retrieval.
	 *
	 * @param jpaUserDao       Data access for user information.
	 * @param jpaLessonDao     Data access for lesson details.
	 * @param jpaCourseDao     Data access for general course data.
	 * @param jpaExerciseDao   Data access for exercise records.
	 * @param jpaUserLessonDao Data access for the relationship between users and lessons.
	 * @param jpaCategoryDao   Data access for course categories.
	 */
	public JpaUserCourseMapper(
		JpaUserDao jpaUserDao,
		JpaLessonDao jpaLessonDao,
		JpaCourseDao jpaCourseDao,
		JpaExerciseDao jpaExerciseDao,
		JpaUserLessonDao jpaUserLessonDao,
		JpaCategoryDao jpaCategoryDao
	) {
		this.jpaUserDao = jpaUserDao;
		this.jpaLessonDao = jpaLessonDao;
		this.jpaCourseDao = jpaCourseDao;
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaCategoryDao = jpaCategoryDao;
	}

	/**
	 * Converts a list of CourseEntity objects into a list of UserCourse domain models. It fetches related user lesson
	 * information in bulk to optimize performance.
	 *
	 * @param userId         The ID of the user.
	 * @param courseEntities List of course entities to map.
	 * @return List of mapped UserCourse domain objects.
	 */
	public List<UserCourse> toUserCourses(int userId, List<CourseEntity> courseEntities) {
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
