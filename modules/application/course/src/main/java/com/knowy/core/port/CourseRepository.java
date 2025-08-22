package com.knowy.core.port;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.exception.KnowyInconsistentDataException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface CourseRepository {
	List<Course> findAllById(List<Integer> ids) throws KnowyInconsistentDataException;

	List<Course> findAll() throws KnowyInconsistentDataException;

	Optional<Course> findById(Integer id) throws KnowyInconsistentDataException;

	Set<Course> findInRandomOrder(int numOfRecords) throws KnowyInconsistentDataException;

	List<Course> findAllRandomOrder() throws KnowyInconsistentDataException;

	Stream<Course> findAllStreamingInRandomOrder();

	Set<Course> findAllWhereUserIsSubscribed(int userId) throws  KnowyInconsistentDataException;;

	Stream<Course> findByCategoriesStreamingInRandomOrder(Collection<Category> categories);
}
