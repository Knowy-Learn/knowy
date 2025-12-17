package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.GenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaGenderDao extends JpaRepository<GenderEntity, Integer> {

	@Query("SELECT g FROM GenderEntity g WHERE g.name = :name")
	Optional<GenderEntity> findByName(@Param("name") String name);
}
