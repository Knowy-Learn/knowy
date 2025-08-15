package com.knowy.server.application;

import com.knowy.server.application.ports.CategoryRepository;
import com.knowy.server.domain.Category;

import java.util.List;

/**
 * Service providing operations related to categories.
 */
public class CategoryService {

	private final CategoryRepository categoryRepository;

	/**
	 * Creates a new {@code CategoryService} with the given repository.
	 *
	 * @param categoryRepository Repository used to fetch category data.
	 */
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}


	/**
	 * Retrieves all categories from the repository.
	 *
	 * @return A list of all {@link Category} entities.
	 */
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}
}
