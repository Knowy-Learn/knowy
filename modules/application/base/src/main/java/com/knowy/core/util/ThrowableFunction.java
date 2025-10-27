package com.knowy.core.util;

public interface ThrowableFunction<T, R> {
	// Functional interface for lambdas that can throw any type of exception
	@SuppressWarnings("java:S112")
	R apply(T param) throws Exception;
}
