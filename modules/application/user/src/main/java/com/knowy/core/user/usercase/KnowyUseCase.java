package com.knowy.core.user.usercase;

import com.knowy.core.exception.KnowyException;

public interface KnowyUseCase<T, R> {

	R execute(T param) throws KnowyException;
}
