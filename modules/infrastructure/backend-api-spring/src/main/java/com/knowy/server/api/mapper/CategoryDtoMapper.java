package com.knowy.server.api.mapper;

import com.knowy.core.domain.CategoryUnidentifiedData;
import com.knowy.server.api.dto.CategoryDto;

import java.util.List;
import java.util.Set;

// JAVADOC
public class CategoryDtoMapper {

	public List<CategoryDto> categoriesToDto(Set<CategoryUnidentifiedData> categories) {
		return categories.stream()
			.map(this::categoryToDto)
			.toList();
	}

	public CategoryDto categoryToDto(CategoryUnidentifiedData category) {
		return new CategoryDto(category.name());
	}
}
