package com.knowy.server.application.usecase;

import com.knowy.server.application.exception.KnowyException;

public interface KnowyUseCase<T, R> {

	R execute(T param) throws KnowyException;
}
