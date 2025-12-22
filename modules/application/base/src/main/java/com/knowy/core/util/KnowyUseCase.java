package com.knowy.core.util;

import com.knowy.core.exception.KnowyException;

public interface KnowyUseCase<T, R> {

	R execute(T param) throws KnowyException;
}
