package com.knowy.persistence.adapter.jpa.dao;

import com.knowy.persistence.adapter.jpa.entity.ProfileImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaProfileImageDao extends JpaRepository<ProfileImageEntity, Integer> {

	Optional<ProfileImageEntity> findById(int id);
}
