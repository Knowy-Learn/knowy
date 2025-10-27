package com.knowy.persistence.adapter.jpa;

import com.knowy.core.user.domain.ProfileImage;
import com.knowy.core.user.port.ProfileImageRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaProfileImageDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaProfileImageMapper;

import java.util.Optional;

public class JpaProfileImageRepository implements ProfileImageRepository {

	private final JpaProfileImageDao jpaProfileImageDao;

	public JpaProfileImageRepository(JpaProfileImageDao jpaProfileImageDao) {
		this.jpaProfileImageDao = jpaProfileImageDao;
	}

	@Override
	public Optional<ProfileImage> findById(int id) {
		JpaProfileImageMapper jpaProfileImageMapper = new JpaProfileImageMapper();

		return jpaProfileImageDao.findById(id).map(jpaProfileImageMapper::toDomain);
	}
}
