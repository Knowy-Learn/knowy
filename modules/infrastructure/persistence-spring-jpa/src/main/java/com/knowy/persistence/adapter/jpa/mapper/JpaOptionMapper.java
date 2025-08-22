package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Option;
import com.knowy.persistence.adapter.jpa.entity.OptionEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(
	value = EntityMapper.class,
	parameterizedContainer = {Option.class, OptionEntity.class}
)
public class JpaOptionMapper implements EntityMapper<Option, OptionEntity> {
	@Override
	public Option toDomain(OptionEntity entity) {
		return new Option(
			entity.getId(),
			entity.getOptionText(),
			entity.isCorrect()
		);
	}

	@Override
	public OptionEntity toEntity(Option domain) {
		return null;
	}
}
