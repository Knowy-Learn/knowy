package com.knowy.core.user.domain;

import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;

public enum Gender {
	MALE,
	FEMALE,
	OTHER,
	UNKNOWN;

	public static Gender fromValue(String value) throws KnowyInvalidUserGenderException {
		if (value == null) {
			return Gender.UNKNOWN;
		}

		try {
			return Gender.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new KnowyInvalidUserGenderException("Invalid gender: " + value, e);
		}
	}
}
