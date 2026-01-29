package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.CourseIdentifiedInfo;
import com.knowy.persistence.adapter.jpa.dao.JpaCategoryDao;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;

import java.util.stream.Collectors;

public class JpaCourseInfoMapper implements EntityMapper<CourseIdentifiedInfo, CourseEntity> {

	private final JpaCategoryDao jpaCategoryDao;

	public JpaCourseInfoMapper(JpaCategoryDao jpaCategoryDao) {
		this.jpaCategoryDao = jpaCategoryDao;
	}

	@Override
	public CourseIdentifiedInfo toDomain(CourseEntity entity) {
		final JpaCategoryMapper jpaCategoryMapper = new JpaCategoryMapper(jpaCategoryDao);

		return new CourseIdentifiedInfo(
			entity.getId(),
			entity.getTitle(),
			entity.getDescription(),
			entity.getImage(),
			entity.getAuthor(),
			entity.getCreationDate(),
			entity.getLanguages().stream()
				.map(jpaCategoryMapper::toDomain)
				.collect(Collectors.toSet())
		);
	}

	@Override
	public CourseEntity toEntity(CourseIdentifiedInfo domain) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
}
