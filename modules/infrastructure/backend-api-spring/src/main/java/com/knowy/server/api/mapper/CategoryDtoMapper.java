package com.knowy.server.api.mapper;

import com.knowy.core.domain.Category;
import com.knowy.server.api.dto.CategoryDto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper utility to convert Category entities to DTOs.
 */
public class CategoryDtoMapper {

	/**
	 * Converts a set of Category entities to a set of CategoryDto objects.
	 *
	 * @param categories the set of categories to map.
	 * @return a set of mapped DTOs, or an empty set if input is null or empty.
	 */
	public Set<CategoryDto> toDto(Set<Category> categories) {
		if (categories == null || categories.isEmpty()) {
			return Set.of();
		}

		return categories.stream()
			.map(this::toDto)
			.collect(Collectors.toSet());
	}

	/**
	 * Maps a single Category entity to a CategoryDto.
	 *
	 * @param category the category entity to map.
	 * @return the mapped CategoryDto.
	 * @throws NullPointerException if the category is null.
	 */
	public CategoryDto toDto(Category category) {
		Objects.requireNonNull(category);
		return new CategoryDto(category.id(), category.name());
	}
}
