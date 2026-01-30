package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface JpaCourseDao extends JpaRepository<CourseEntity, Integer> {
	@NonNull
	List<CourseEntity> findAll();

	@Query("SELECT c FROM CourseEntity c ORDER BY function('RANDOM')")
	Stream<CourseEntity> findAllRandom();

	@Query("""
		SELECT c
		FROM CourseEntity c
		    JOIN c.lessons l
		    JOIN PublicUserLessonEntity pul
		        ON pul.lessonEntity = l
		WHERE pul.userId = :userId
		GROUP BY c
		ORDER BY AVG(CASE
		    WHEN pul.status = 'completed' THEN 3
		    WHEN pul.status = 'in_progress' THEN 2
		    WHEN pul.status = 'pending' THEN 1
		    ELSE 0
		END) DESC
		""")
	Page<CourseEntity> findAllByUserId(@Param("userId") int userId, Pageable pageable);

	@Query("""
		SELECT c
		FROM CourseEntity c
		    JOIN c.languages lang
		WHERE lang.id IN (:categoriesIds)
		ORDER BY function('RANDOM')
		""")
	Stream<CourseEntity> findByCategoryIdsInRandomOrder(@Param("categoriesIds") List<Integer> categoriesIds);

	@Query("SELECT l.course FROM LessonEntity l WHERE l.id = :lessonId")
	CourseEntity findCourseIdByLessonId(@Param("lessonId") int lessonId);
}

