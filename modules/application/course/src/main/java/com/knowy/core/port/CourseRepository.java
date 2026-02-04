package com.knowy.core.port;

import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.data.KnowyDataAccessException;
import com.knowy.core.exception.data.KnowyInconsistentDataException;

import java.util.*;
import java.util.stream.Stream;

// TODO: Change KnowyInconsistentDataException to KnowyDataAccessException
public interface CourseRepository {

	<T extends CourseUnidentifiedData> List<Course> saveAll(List<T> courses) throws KnowyInconsistentDataException;

	List<Course> findAllById(List<Integer> ids) throws KnowyInconsistentDataException;

	PagedResult<Course> findAll(Pagination pagination) throws KnowyCourseNotFound;

	PagedResult<Course> findAllRandomUnsubscribedUsers(int userId, Pagination pagination) throws KnowyDataAccessException;

	Set<Course> findInRandomOrder(int numOfRecords) throws KnowyInconsistentDataException;

	List<Course> findAllRandomOrder() throws KnowyInconsistentDataException;

	Stream<Course> findAllStreamingInRandomOrder();

	Set<Course> findAllWhereUserIsSubscribed(int userId) throws KnowyInconsistentDataException;

	Stream<Course> findByCategoriesStreamingInRandomOrder(Collection<Category> categories);

	Optional<Course> findById(Integer id) throws KnowyInconsistentDataException;

	OptionalInt findCourseIdByLessonId(int lessonId) throws KnowyInconsistentDataException;
}
