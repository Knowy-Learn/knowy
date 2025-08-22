package com.knowy.persistence.adapter.jpa;

import com.knowy.core.user.domain.ProfileImage;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaProfileImageDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaProfileImageMapper;

import java.util.Optional;

public class JpaProfileImageRepository implements ProfileImageRepository {

	private final JpaProfileImageDao jpaProfileImageDao;
	private final JpaProfileImageMapper jpaProfileImageMapper;

	public JpaProfileImageRepository(JpaProfileImageDao jpaProfileImageDao, JpaProfileImageMapper jpaProfileImageMapper) {
		this.jpaProfileImageDao = jpaProfileImageDao;
		this.jpaProfileImageMapper = jpaProfileImageMapper;
	}

	@Override
	public Optional<ProfileImage> findById(int id) {
		return jpaProfileImageDao.findById(id).map(jpaProfileImageMapper::toDomain);
	}
}
