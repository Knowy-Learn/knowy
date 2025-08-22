package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.ProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaProfileImageDao extends JpaRepository<ProfileImageEntity, Integer> {

	Optional<ProfileImageEntity> findById(int id);
}
