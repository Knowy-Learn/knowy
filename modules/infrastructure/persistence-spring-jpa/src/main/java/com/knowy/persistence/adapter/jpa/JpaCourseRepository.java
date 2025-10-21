package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.domain.CourseUnidentifiedData;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JpaCourseRepository implements CourseRepository {

	private final JpaCourseDao jpaCourseDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaCategoryDao jpaCategoryDao;
	private final JpaLessonDao jpaLessonDao;
	private final JpaExerciseDao jpaExerciseDao;

	public JpaCourseRepository(
		JpaCourseDao jpaCourseDao,
		JpaUserLessonDao jpaUserLessonDao,
		JpaCategoryDao jpaCategoryDao,
		JpaLessonDao jpaLessonDao,
		JpaExerciseDao jpaExerciseDao
	) {
		this.jpaCourseDao = jpaCourseDao;
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaCategoryDao = jpaCategoryDao;
		this.jpaLessonDao = jpaLessonDao;
		this.jpaExerciseDao = jpaExerciseDao;
	}

	@Override
	@Transactional
	public <T extends CourseUnidentifiedData> List<Course> saveAll(List<T> courses) throws KnowyInconsistentDataException {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		List<CourseEntity> courseEntities = new ArrayList<>();
		for (T course : courses) {
			courseEntities.add(jpaCourseMapper.toEntity(course));
		}
		return courseEntities.stream()
			.map(jpaCourseDao::saveAndFlush)
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	@Override
	public List<Course> findAllById(List<Integer> ids) {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		return jpaCourseDao.findAllById(ids)
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	@Override
	public List<Course> findAll(Pagination pagination) throws KnowyCourseNotFound {
		List<Course> courses = fetchCourses(pagination);
		validateCourses(courses, pagination.page());
		return courses;
	}

	private List<Course> fetchCourses(Pagination pagination) {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		Pageable pageRequest = PageRequest.of(pagination.page(), pagination.size());
		return jpaCourseDao.findAll(pageRequest)
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	private void validateCourses(List<Course> courses, int page) throws KnowyCourseNotFound {
		if (courses.isEmpty()) {
			String message = String.format("Courses not found for the page: %d", page);
			throw new KnowyCourseNotFound(message);
		}
	}

	@Override
	public Set<Course> findInRandomOrder(int numOfRecords) {
		return findAllStreamingInRandomOrder()
			.limit(numOfRecords)
			.collect(Collectors.toSet());
	}

	@Override
	public List<Course> findAllRandomOrder() {
		return findAllStreamingInRandomOrder()
			.toList();
	}

	@Override
	public Stream<Course> findAllStreamingInRandomOrder() {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		return jpaCourseDao.findAllRandom()
			.map(jpaCourseMapper::toDomain);
	}

	@Override
	public Set<Course> findAllWhereUserIsSubscribed(int userId) {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		return jpaUserLessonDao.findByUserId(userId).stream()
			.map(PublicUserLessonEntity::getLessonEntity)
			.map(LessonEntity::getCourse)
			.map(jpaCourseMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public Stream<Course> findByCategoriesStreamingInRandomOrder(Collection<Category> categories) {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		List<Integer> categoriesIds = categories.stream()
			.map(Category::id)
			.toList();
		return jpaCourseDao.findByCategoryIdsInRandomOrder(categoriesIds)
			.map(jpaCourseMapper::toDomain);
	}

	@Override
	public Optional<Course> findById(Integer id) {
		JpaCourseMapper jpaCourseMapper = getNewJpaCourseMapper();

		return jpaCourseDao.findById(id).map(jpaCourseMapper::toDomain);
	}

	private JpaCourseMapper getNewJpaCourseMapper() {
		return new JpaCourseMapper(
			new JpaCategoryMapper(jpaCategoryDao),
			new JpaLessonMapper(
				new JpaDocumentationMapper(jpaLessonDao),
				new JpaExerciseMapper(
					new JpaOptionMapper(jpaExerciseDao),
					jpaLessonDao
				),
				jpaCourseDao,
				jpaLessonDao
			)
		);
	}

	@Override
	public OptionalInt findCourseIdByLessonId(int lessonId) {
		throw new UnsupportedOperationException("Method not implemented");

	}
}
