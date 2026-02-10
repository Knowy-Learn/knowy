package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.core.port.CourseRepository;
import com.knowy.persistence.adapter.jpa.dao.*;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaCourseMapper;
import com.knowy.persistence.adapter.spring.mapper.SpringPaginationMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.knowy.core.util.CommonUtils.nonEmptyElse;

public class JpaCourseRepository implements CourseRepository {

	private final JpaCourseDao jpaCourseDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaCategoryDao jpaCategoryDao;
	private final JpaLessonDao jpaLessonDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final Random random = new Random();

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

	/**
	 * Saves a list of course data objects into the database.
	 * <p>
	 * Each input object is mapped to a {@link CourseEntity} using {@link JpaCourseMapper}, persisted via
	 * {@code jpaCourseDao}, and then mapped back to the domain {@link Course} object.
	 *
	 * @param courses the list of course data objects to save; must extend {@link CourseUnidentifiedData}
	 * @param <T>     the type of the input course objects
	 * @return a list of {@link Course} objects after being persisted
	 * @throws KnowyInconsistentDataException if any of the input data objects are inconsistent
	 */
	@Override
	@Transactional
	public <T extends CourseUnidentifiedData> List<Course> saveAll(List<T> courses) throws KnowyInconsistentDataException {
		JpaCourseMapper jpaCourseMapper = getJpaCourseMapper();

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
		JpaCourseMapper jpaCourseMapper = getJpaCourseMapper();

		return jpaCourseDao.findAllById(ids)
			.stream()
			.map(jpaCourseMapper::toDomain)
			.toList();
	}

	@Override
	public PagedResult<Course> findAll(Pagination pagination) throws KnowyCourseNotFound {
		Pageable pageRequest = PageRequest.of(pagination.page().number(), pagination.page().size());
		Page<CourseEntity> pageResult = jpaCourseDao.findAll(pageRequest);

		if (!pageResult.hasContent()) {
			throw new KnowyCourseNotFound(String.format("Courses not found for page: %d", pagination.page().number()));
		}

		return new PagedResult<>(
			pagination.page(),
			pageResult.map(getJpaCourseMapper()::toDomain).getContent(),
			pageResult.getTotalElements()
		);
	}

	@Override
	public PagedResult<Course> findAllRandomUnsubscribedUsers(int userId, Pagination pagination) {
		var courseMapper = new JpaCourseMapper(jpaCategoryDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao);

		Pageable pageable = new SpringPaginationMapper().toPageableWithoutSort(pagination);
		Set<Integer> categoryIds = extractCategoryIds(pagination.filters());

		Page<CourseEntity> courseEntities = jpaCourseDao.findAllRandomUnsubscribedUsers(
			userId,
			nonEmptyElse(categoryIds, null),
			random.nextInt(100),
			pageable
		);
		List<Course> courses = courseEntities.getContent().stream()
			.map(courseMapper::toDomain)
			.toList();

		return new PagedResult<>(pagination.page(), courses, courseEntities.getTotalElements());
	}

	private Set<Integer> extractCategoryIds(Set<Filter> filters) {
		return filters.stream()
			.filter(f -> "category".equals(f.fieldName()))
			.flatMap(f -> f.value() instanceof Set<?> s ? s.stream() : Stream.empty())
			.filter(Category.class::isInstance)
			.map(Category.class::cast)
			.map(Category::id)
			.collect(Collectors.toSet());
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
		JpaCourseMapper jpaCourseMapper = getJpaCourseMapper();

		return jpaCourseDao.findAllRandom()
			.map(jpaCourseMapper::toDomain);
	}

	@Override
	public Set<Course> findAllWhereUserIsSubscribed(int userId) {
		JpaCourseMapper jpaCourseMapper = getJpaCourseMapper();

		return jpaUserLessonDao.findByUserId(userId).stream()
			.map(PublicUserLessonEntity::getLessonEntity)
			.map(LessonEntity::getCourse)
			.map(jpaCourseMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public Stream<Course> findByCategoriesStreamingInRandomOrder(Collection<Category> categories) {
		JpaCourseMapper jpaCourseMapper = getJpaCourseMapper();

		List<Integer> categoriesIds = categories.stream()
			.map(Category::id)
			.toList();
		return jpaCourseDao.findByCategoryIdsInRandomOrder(categoriesIds)
			.map(jpaCourseMapper::toDomain);
	}

	@Override
	public Optional<Course> findById(Integer id) {
		JpaCourseMapper jpaCourseMapper = getJpaCourseMapper();

		return jpaCourseDao.findById(id).map(jpaCourseMapper::toDomain);
	}

	private JpaCourseMapper getJpaCourseMapper() {
		return new JpaCourseMapper(jpaCategoryDao, jpaLessonDao, jpaCourseDao, jpaExerciseDao);
	}

	@Override
	public OptionalInt findCourseIdByLessonId(int lessonId) {
		throw new UnsupportedOperationException("Method not implemented");

	}
}
