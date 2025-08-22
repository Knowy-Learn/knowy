package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Category;
import com.knowy.core.port.CategoryRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaCategoryMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@ConditionalOnMissingBean(CategoryRepository.class)
public class JpaCategoryRepository implements CategoryRepository {

	private final JpaCategoryDao jpaCategoryDao;
	private final JpaCategoryMapper jpaCategoryMapper;

	public JpaCategoryRepository(JpaCategoryDao jpaCategoryDao, JpaCategoryMapper jpaCategoryMapper) {
		this.jpaCategoryDao = jpaCategoryDao;
		this.jpaCategoryMapper = jpaCategoryMapper;
	}

	@Override
	public Set<Category> findByNameInIgnoreCase(String[] names) {
		return jpaCategoryDao.findByNameInIgnoreCase(names)
			.stream()
			.map(jpaCategoryMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public List<Category> findAll() {
		return jpaCategoryDao.findAll()
			.stream()
			.map(jpaCategoryMapper::toDomain)
			.toList();
	}
}
