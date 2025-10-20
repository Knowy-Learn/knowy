package com.knowy.persistence.adapter.jpa.mapper;

import com.knowy.core.domain.Option;
import com.knowy.core.domain.OptionData;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.persistence.adapter.jpa.dao.JpaExerciseDao;
import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import com.knowy.persistence.adapter.jpa.entity.OptionEntity;
import org.springframework.stereotype.Component;

@Component
public class JpaOptionMapper implements EntityMapper<Option, OptionEntity> {

	private final JpaExerciseDao jpaExerciseDao;

	public JpaOptionMapper(JpaExerciseDao jpaExerciseDao) {
		this.jpaExerciseDao = jpaExerciseDao;
	}

	@Override
	public Option toDomain(OptionEntity entity) {
		return new Option(
			entity.getId(),
			entity.getOptionText(),
			entity.isCorrect()
		);
	}

	@Override
	public OptionEntity toEntity(Option domain) throws KnowyInconsistentDataException {
		return new OptionEntity(
			domain.id(),
			jpaExerciseDao.findByOptionId(domain.id())
				.orElseThrow(() -> new KnowyInconsistentDataException(
					"Exercise not found by option id: " + domain.id()
				)),
			domain.value(),
			domain.isValid()
		);
	}

	public <T extends OptionData> OptionEntity toEntity(T domain, ExerciseEntity exercise) {
		OptionEntity option = new OptionEntity();
		option.setId(null);
		option.setOptionText(domain.value());
		option.setCorrect(domain.isValid());
		option.setExercise(exercise);

		return option;
	}
}
