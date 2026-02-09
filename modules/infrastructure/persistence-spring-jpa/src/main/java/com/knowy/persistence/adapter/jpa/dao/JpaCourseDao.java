package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface JpaCourseDao extends JpaRepository<CourseEntity, Integer> {
	@NonNull
	List<CourseEntity> findAll();

	@Query("""
		SELECT c
		FROM CourseEntity c
		JOIN c.languages lang
		WHERE NOT EXISTS (
		    SELECT 1
		    FROM PublicUserLessonEntity pul
		    JOIN pul.lessonEntity l
		    WHERE l.course = c AND pul.userId = :userId
		) AND (:categories IS NULL OR lang.id IN :categories)
		""")
	Page<CourseEntity> findAllRandomUnsubscribedUsers(
		@Param("userId") int userId,
		@Param("categories") @Nullable Set<Integer> categories,
		Pageable pageable
	);

	@Query("SELECT c FROM CourseEntity c ORDER BY function('RANDOM')")
	Stream<CourseEntity> findAllRandom();

	@Query("""
		    SELECT c
		    FROM CourseEntity c
		        JOIN c.lessons l
		        JOIN PublicUserLessonEntity pul
		            ON pul.lessonEntity = l
		        JOIN c.languages lang
		    WHERE pul.userId = :userId
		        AND (:categories IS NULL OR lang.name IN :categories)
		    GROUP BY c
		    HAVING
		        CASE AVG(
		            CASE pul.status
		                WHEN 'completed' THEN 1
		                WHEN 'pending' THEN 0
		                ELSE 0.5
		            END
		        )
		        WHEN 0 THEN 2 /* PENDING */
		        WHEN 1 THEN 0 /* COMPLETED */
		        ELSE 1        /* IN_PROGRESS */
		    END IN (:courseStatusIds)
		    ORDER BY AVG(CASE pul.status
		        WHEN 'completed' THEN 1
		        WHEN 'pending' THEN 0
		        ELSE 0.5
		    END) DESC
		""")
	Page<CourseEntity> findAllByUserId(
		@Param("userId") int userId,
		@Param("courseStatusIds") Set<Integer> courseStatusIds,
		@Param("categories") @Nullable Set<String> categories,
		Pageable pageable
	);

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

