package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.user.domain.User;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.dao.JpaGenderDao;
import com.knowy.persistence.adapter.jpa.entity.PublicUserEntity;

import java.util.stream.Collectors;

public class JpaUserMapper implements EntityMapper<User, PublicUserEntity> {

	private final JpaCategoryMapper jpaCategoryMapper;
	private final JpaProfileImageMapper jpaProfileImageMapper;
	private final JpaGenderMapper jpaGenderMapper;

	public JpaUserMapper(JpaCategoryDao jpaCategoryDao, JpaGenderDao jpaGenderDao) {
		this.jpaCategoryMapper = new JpaCategoryMapper(jpaCategoryDao);
		this.jpaProfileImageMapper = new JpaProfileImageMapper();
		this.jpaGenderMapper = new JpaGenderMapper(jpaGenderDao);
	}

	@Override
	public User toDomain(PublicUserEntity entity) throws KnowyInvalidUserGenderException {
		return new User(
			entity.getId(),
			entity.getNickname(),
			jpaGenderMapper.toDomain(entity.getGender()),
			jpaProfileImageMapper.toDomain(entity.getProfileImage()),
			entity.getLanguages().stream()
				.map(jpaCategoryMapper::toDomain)
				.collect(Collectors.toSet())
		);
	}

	@Override
	public PublicUserEntity toEntity(User domain) {
		return new PublicUserEntity(
			domain.id(),
			domain.nickname(),
			jpaGenderMapper.toEntity(domain.gender()),
			jpaProfileImageMapper.toEntity(domain.profileImage()),
			domain.categories().stream()
				.map(jpaCategoryMapper::toEntity)
				.collect(Collectors.toSet())
		);
	}
}
