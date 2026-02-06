package com.knowy.core.util;

import java.util.Collection;

public class CommonUtils {

	private CommonUtils() {
	}

	public static <T extends Collection<?>> T nonEmptyElse(T collection, T defaultValue) {
		return collection.isEmpty() ? defaultValue : collection;
	}
}
