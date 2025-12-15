package com.knowy.core.user.usercase;

import com.knowy.core.exception.KnowyException;
import com.knowy.core.user.exception.validation.KnowyInvalidUserGenderException;

public interface KnowyUseCase<T, R> {

	R execute(T param) throws KnowyException, KnowyInvalidUserGenderException;
}
