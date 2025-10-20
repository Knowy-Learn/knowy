package com.knowy.core.port;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.domain.CourseUnidentifiedData;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyInconsistentDataException;

import java.util.*;
import java.util.stream.Stream;

public interface CourseRepository {

	<T extends CourseUnidentifiedData> List<Course> saveAll(List<T> courses) throws KnowyInconsistentDataException;

	List<Course> findAllById(List<Integer> ids) throws KnowyInconsistentDataException;

	List<Course> findAll(Pagination pagination) throws KnowyCourseNotFound;

	Set<Course> findInRandomOrder(int numOfRecords) throws KnowyInconsistentDataException;

	List<Course> findAllRandomOrder() throws KnowyInconsistentDataException;

	Stream<Course> findAllStreamingInRandomOrder();

	Set<Course> findAllWhereUserIsSubscribed(int userId) throws KnowyInconsistentDataException;

	Stream<Course> findByCategoriesStreamingInRandomOrder(Collection<Category> categories);

	Optional<Course> findById(Integer id) throws KnowyInconsistentDataException;

	OptionalInt findCourseIdByLessonId(int lessonId) throws KnowyInconsistentDataException;
}
