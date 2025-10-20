package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.CategoryData;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class JpaCategoryMapper implements EntityMapper<Category, CategoryEntity> {

	private final JpaCategoryDao jpaCategoryDao;

	public JpaCategoryMapper(JpaCategoryDao jpaCategoryDao) {
		this.jpaCategoryDao = jpaCategoryDao;
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
		return jpaCategoryDao.findByName(domain.name())
			.orElseGet(() -> {
				CategoryEntity categoryEntity = new CategoryEntity();
				categoryEntity.setName(domain.name());
				return jpaCategoryDao.saveAndFlush(categoryEntity);
			}
		);
	}
}
