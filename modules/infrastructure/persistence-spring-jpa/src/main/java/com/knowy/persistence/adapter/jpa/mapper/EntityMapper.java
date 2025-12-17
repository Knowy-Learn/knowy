package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.exception.KnowyException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;

/**
 * @param <T> The domain type
 * @param <E> The entity type
 */
public interface EntityMapper<T, E> {

	T toDomain(E entity) throws KnowyException, KnowyInvalidUserGenderException;

	E toEntity(T domain) throws KnowyException;
}
