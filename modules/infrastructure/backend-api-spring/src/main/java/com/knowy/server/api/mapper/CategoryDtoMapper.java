package com.knowy.server.api.mapper;

import com.knowy.core.domain.Category;
import com.knowy.server.api.dto.CategoryDto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// JAVADOC
public class CategoryDtoMapper {

	public Set<CategoryDto> toDto(Set<Category> categories) {
		if (categories == null || categories.isEmpty()) {
			return Set.of();
		}

		return categories.stream()
			.map(this::toDto)
			.collect(Collectors.toSet());
	}

	public CategoryDto toDto(Category category) {
		Objects.requireNonNull(category);
		return new CategoryDto(category.id(), category.name());
	}
}
