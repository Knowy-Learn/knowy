package com.knowy.core.domain;

public interface CategoryUnidentifiedData {

	String name();

	record InmutableCategoryUnidentifiedData(String name) implements CategoryUnidentifiedData {
	}
}
