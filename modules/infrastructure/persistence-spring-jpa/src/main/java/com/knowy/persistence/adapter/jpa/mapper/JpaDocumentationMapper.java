package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Documentation;
import com.knowy.persistence.adapter.jpa.dao.JpaLessonDao;
import com.knowy.persistence.adapter.jpa.entity.DocumentationEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {Documentation.class, DocumentationEntity.class}
)
public class JpaDocumentationMapper implements EntityMapper<Documentation, DocumentationEntity> {

	private final JpaLessonDao jpaLessonDao;

	public JpaDocumentationMapper(JpaLessonDao jpaLessonDao) {
		this.jpaLessonDao = jpaLessonDao;
	}

	@Override
	public Documentation toDomain(DocumentationEntity entity) {
		return new Documentation(entity.getId(), entity.getTitle(), entity.getLink());
	}

	@Override
	public DocumentationEntity toEntity(Documentation domain) {
		return new DocumentationEntity(
			domain.id(),
			domain.title(),
			domain.link(),
			jpaLessonDao.findAllByDocumentationId(domain.id())
		);
	}
}
