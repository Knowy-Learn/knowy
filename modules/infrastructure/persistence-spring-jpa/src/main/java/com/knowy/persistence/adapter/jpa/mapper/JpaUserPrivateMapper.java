package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.user.domain.Email;
import com.knowy.core.user.domain.Password;
import com.knowy.core.user.domain.UserPrivate;
import com.knowy.persistence.adapter.jpa.entity.PrivateUserEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {UserPrivate.class, PrivateUserEntity.class}
)
public class JpaUserPrivateMapper implements EntityMapper<UserPrivate, PrivateUserEntity> {

	private final JpaUserMapper jpaUserMapper;

	public JpaUserPrivateMapper(JpaUserMapper jpaUserMapper) {
		this.jpaUserMapper = jpaUserMapper;
	}

	@Override
	public UserPrivate toDomain(PrivateUserEntity entity) {
		return new UserPrivate(
			jpaUserMapper.toDomain(entity.getPublicUserEntity()),
			new Email(entity.getEmail()),
			new Password(entity.getPassword()),
			entity.isActive()
		);
	}

	@Override
	public PrivateUserEntity toEntity(UserPrivate domain) {
		PrivateUserEntity privateUserEntity = new PrivateUserEntity();
		privateUserEntity.setId(domain.id());
		privateUserEntity.setEmail(domain.email().value());
		privateUserEntity.setPassword(domain.password().value());
		privateUserEntity.setActive(domain.active());
		privateUserEntity.setPublicUserEntity(jpaUserMapper.toEntity(domain.cropToUser()));
		return privateUserEntity;
	}
}
