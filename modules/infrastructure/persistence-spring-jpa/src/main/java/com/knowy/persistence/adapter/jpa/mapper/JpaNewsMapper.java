package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.News;
import com.knowy.persistence.adapter.jpa.entity.NewsEntity;

public class JpaNewsMapper implements EntityMapper<News, NewsEntity> {

	@Override
	public News toDomain(NewsEntity entity) {
		return new News(entity.getId(), entity.getTitle(), entity.getContent(), entity.getDate());
	}

	@Override
	public NewsEntity toEntity(News domain) {
		return new NewsEntity(domain.id(), domain.title(), domain.content(), domain.date());
	}
}
