package com.knowy.core.port;

import com.knowy.core.domain.Category;

import java.util.List;
import java.util.Set;

public interface CategoryRepository {
	Set<Category> findByNameInIgnoreCase(String[] names);

	List<Category> findAll();
}
