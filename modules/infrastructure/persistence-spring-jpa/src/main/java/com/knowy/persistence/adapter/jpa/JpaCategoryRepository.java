package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.Category;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CategoryRepository;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.mapper.JpaCategoryMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JpaCategoryRepository implements CategoryRepository {

	private final JpaCategoryDao jpaCategoryDao;

	public JpaCategoryRepository(JpaCategoryDao jpaCategoryDao) {
		this.jpaCategoryDao = jpaCategoryDao;
	}

	@Override
	public Optional<Category> findByName(String name) throws KnowyInconsistentDataException {
		JpaCategoryMapper jpaCategoryMapper = new JpaCategoryMapper(jpaCategoryDao);

		return jpaCategoryDao.findByName(name).map(jpaCategoryMapper::toDomain);
	}

	@Override
	public Set<Category> findByNameInIgnoreCase(String[] names) throws KnowyInconsistentDataException {
		JpaCategoryMapper jpaCategoryMapper = new JpaCategoryMapper(jpaCategoryDao);

		return jpaCategoryDao.findByNameInIgnoreCase(names).stream()
			.map(jpaCategoryMapper::toDomain)
			.collect(Collectors.toSet());
	}

	@Override
	public List<Category> findAll() {
		JpaCategoryMapper jpaCategoryMapper = new JpaCategoryMapper(jpaCategoryDao);

		return jpaCategoryDao.findAll().stream()
			.map(jpaCategoryMapper::toDomain)
			.toList();
	}
}
