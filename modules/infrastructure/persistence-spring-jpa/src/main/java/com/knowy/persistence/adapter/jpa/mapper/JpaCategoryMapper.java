package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.CategoryData;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.entity.CategoryEntity;

import java.util.*;

public class JpaCategoryMapper implements EntityMapper<Category, CategoryEntity> {

	private final JpaCategoryDao jpaCategoryDao;
	private final Map<CategoryEntity, CategoryEntity> categories;

	public JpaCategoryMapper(JpaCategoryDao jpaCategoryDao) {
		this.jpaCategoryDao = jpaCategoryDao;
		this.categories = new HashMap<>();
	}

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

	public <T extends CategoryData> CategoryEntity toEntity(T domain) throws KnowyInconsistentDataException {
		CategoryEntity newCategory = new CategoryEntity();
		newCategory.setName(domain.name());

		if (categories.containsKey(newCategory)) {
			return categories.get(newCategory);
		}

		Optional<CategoryEntity> databaseCategory = jpaCategoryDao.findByName(domain.name());
		if (databaseCategory.isPresent()) {
			return databaseCategory.get();
		}

		categories.put(newCategory, newCategory);
		return newCategory;
	}
}
