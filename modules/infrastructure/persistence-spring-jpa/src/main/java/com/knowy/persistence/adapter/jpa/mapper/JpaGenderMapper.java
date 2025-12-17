package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.exception.validation.KnowyIllegalArgumentRuntimeException;
import com.knowy.core.user.domain.Gender;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;
import com.knowy.persistence.adapter.jpa.dao.JpaGenderDao;
import com.knowy.persistence.adapter.jpa.entity.GenderEntity;

public class JpaGenderMapper implements EntityMapper<Gender, GenderEntity> {

	private final JpaGenderDao genderDao;

	public JpaGenderMapper(JpaGenderDao genderDao) {
		this.genderDao = genderDao;
	}

	@Override
	public Gender toDomain(GenderEntity entity) throws KnowyInvalidUserGenderException {
		return Gender.fromValue(entity.getName());
	}

	@Override
	public GenderEntity toEntity(Gender domain) {
		String name = domain.toString();
		return genderDao.findByName(name)
			.orElseThrow(() -> new KnowyIllegalArgumentRuntimeException("Gender not found in database for name: " + name));
	}
}
