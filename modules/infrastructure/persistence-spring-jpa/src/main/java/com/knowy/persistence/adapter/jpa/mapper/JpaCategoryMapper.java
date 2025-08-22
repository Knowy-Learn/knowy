package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Category;
import com.knowy.persistence.adapter.jpa.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class JpaCategoryMapper implements EntityMapper<Category, CategoryEntity> {

	@Override
	public Category toDomain(CategoryEntity entity) {
		return new Category(entity.getId(), entity.getName());
	}

	@Override
	public CategoryEntity toEntity(Category domain) {
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setId(domain.id());
		categoryEntity.setName(domain.name());
		return categoryEntity;
	}
}
