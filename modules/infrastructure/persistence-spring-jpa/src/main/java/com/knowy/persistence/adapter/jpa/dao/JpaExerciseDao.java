package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaExerciseDao extends JpaRepository<ExerciseEntity, Integer> {

	@Query("""
		SELECT e
		FROM OptionEntity o
		JOIN o.exercise e
		WHERE o.id = :optionId
		""")
	Optional<ExerciseEntity> findByOptionId(@Param("optionId") int optionId);
}
