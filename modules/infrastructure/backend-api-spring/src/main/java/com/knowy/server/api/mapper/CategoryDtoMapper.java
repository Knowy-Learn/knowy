package com.knowy.server.api.mapper;

import com.knowy.core.domain.Category;
import com.knowy.server.api.dto.CategoryDto;

import java.util.List;
import java.util.Set;

// JAVADOC
public class CategoryDtoMapper {

	public List<CategoryDto> categoriesToDto(Set<Category> categories) {
		return categories.stream()
			.map(this::categoryToDto)
			.toList();
	}

	public CategoryDto categoryToDto(Category category) {
		return new CategoryDto(category.id(), category.name());
	}
}
