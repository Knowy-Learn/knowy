package com.knowy.core;

import java.io.InputStream;

public interface Importer<T> {
	T execute(InputStream inputStream);
}
