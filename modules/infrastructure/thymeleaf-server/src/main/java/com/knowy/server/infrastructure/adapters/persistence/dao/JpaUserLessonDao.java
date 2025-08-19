package com.knowy.server.infrastructure.adapters.persistence.dao;

import com.knowy.server.infrastructure.adapters.persistence.entity.PublicUserLessonEntity;
import com.knowy.server.infrastructure.adapters.persistence.entity.PublicUserLessonIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JpaUserLessonDao extends JpaRepository<PublicUserLessonEntity, PublicUserLessonIdEntity> {

	boolean existsByUserIdAndLessonId(Integer userId, Integer lessonId);

	@Query("""
		SELECT DISTINCT l.course.id
		FROM PublicUserLessonEntity pul
		JOIN LessonEntity l ON pul.lessonId= l.id
		WHERE pul.userId = :userId
		""")
	List<Integer> findCourseIdsByUserId(@Param("userId") Integer userId);

	boolean existsById(@NonNull PublicUserLessonIdEntity id);

	@NonNull
	<S extends PublicUserLessonEntity> S save(@NonNull S entity);

	@Query("""
		SELECT COUNT(pul)
		FROM PublicUserLessonEntity pul
		JOIN LessonEntity l ON pul.lessonId = l.id
		WHERE pul.userId = :userId AND l.course.id = :courseId AND pul.status = :status
		""")
	int countByUserIdAndCourseIdAndStatus(
		@Param("userId") Integer userId,
		@Param("courseId") Integer courseId,
		@Param("status") String status
	);

	@NonNull
	Optional<PublicUserLessonEntity> findById(@NonNull PublicUserLessonIdEntity publicUserLessonIdEntity);

	@Query("""
		SELECT pul
		FROM PublicUserLessonEntity pul
		    JOIN pul.lessonEntity l
		WHERE pul.userId = :userId
			AND l.course.id = :courseId
		""")
	List<PublicUserLessonEntity> findAllByUserIdAndCourseId(@Param("userId") int userId, @Param("courseId") int courseId);

    Set<PublicUserLessonEntity> findByUserId(int userId);
}
