package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.user.domain.ProfileImage;
import com.knowy.persistence.adapter.jpa.entity.ProfileImageEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {ProfileImage.class, ProfileImageEntity.class}
)
public class JpaProfileImageMapper implements EntityMapper<ProfileImage, ProfileImageEntity> {
	@Override
	public ProfileImage toDomain(ProfileImageEntity entity) {
		return new ProfileImage(entity.getId(), entity.getUrl());
	}

	@Override
	public ProfileImageEntity toEntity(ProfileImage domain) {
		ProfileImageEntity profileImageEntity = new ProfileImageEntity();
		profileImageEntity.setId(domain.id());
		profileImageEntity.setUrl(domain.url());
		return profileImageEntity;
	}
}
