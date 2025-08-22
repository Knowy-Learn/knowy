package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.port.CourseRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCourseDao;
import com.knowy.persistence.adapter.jpa.dao.JpaUserLessonDao;
import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserLessonEntity;
import com.knowy.persistence.adapter.jpa.mapper.JpaCourseMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class JpaCourseRepository implements CourseRepository {

	private final JpaCourseDao jpaCourseDao;
	private final JpaUserLessonDao jpaUserLessonDao;
	private final JpaCourseMapper jpaCourseMapper;

	public JpaCourseRepository(JpaCourseDao jpaCourseDao, JpaUserLessonDao jpaUserLessonDao, JpaCourseMapper jpaCourseMapper) {
		this.jpaCourseDao = jpaCourseDao;
		this.jpaUserLessonDao = jpaUserLessonDao;
		this.jpaCourseMapper = jpaCourseMapper;
	}

	@Override
	public List<Course> findAllById(List<Integer> ids) {
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
	public Optional<Course> findById(Integer id) {
		return jpaCourseDao.findById(id).map(jpaCourseMapper::toDomain);
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
		return jpaCourseDao.findAllRandom()
			.map(jpaCourseMapper::toDomain);
	}

	@Override
	public Set<Course> findAllWhereUserIsSubscribed(int userId) {
		return jpaUserLessonDao.findByUserId(userId).stream()
			.map(PublicUserLessonEntity::getLessonEntity)
			.map(LessonEntity::getCourse)
			.map(jpaCourseMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public Stream<Course> findByCategoriesStreamingInRandomOrder(Collection<Category> categories) {
		List<Integer> categoriesIds = categories.stream()
			.map(Category::id)
			.toList();
		return jpaCourseDao.findByCategoryIdsInRandomOrder(categoriesIds);
	}
}
