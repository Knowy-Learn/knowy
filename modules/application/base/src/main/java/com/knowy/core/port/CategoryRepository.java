package com.knowy.core.port;

import com.knowy.core.domain.Category;
import com.knowy.core.exception.KnowyInconsistentDataException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository {

	Optional<Category> findByName(String name) throws KnowyInconsistentDataException;

	Set<Category> findByNameInIgnoreCase(String[] names) throws KnowyInconsistentDataException;

	List<Category> findAll();
}
