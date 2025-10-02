package com.knowy.core.domain;

public interface CategoryData {

	String name();

	record InmutableCategoryData(String name) implements CategoryData {
	}
}
