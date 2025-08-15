package com.knowy.server.infrastructure.adapters.persistence;

import com.knowy.server.domain.UserExercise;
import com.knowy.server.application.exception.data.inconsistent.notfound.KnowyExerciseNotFoundException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.user.exception.KnowyUserNotFoundException;
import com.knowy.server.application.ports.UserExerciseRepository;
import com.knowy.server.infrastructure.adapters.persistence.dao.JpaExerciseDao;
import com.knowy.server.infrastructure.adapters.persistence.dao.JpaUserDao;
import com.knowy.server.infrastructure.adapters.persistence.dao.JpaUserExerciseDao;
import com.knowy.server.infrastructure.adapters.persistence.entity.PublicUserExerciseEntity;
import com.knowy.server.infrastructure.adapters.persistence.entity.PublicUserExerciseId;
import com.knowy.server.infrastructure.adapters.persistence.mapper.EntityMapper;
import com.knowy.server.infrastructure.adapters.persistence.mapper.JpaExerciseMapper;
import com.knowy.server.infrastructure.adapters.persistence.mapper.JpaUserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserExerciseRepository implements UserExerciseRepository {

	private final JpaUserDao jpaUserDao;
	private final JpaExerciseDao jpaExerciseDao;
	private final JpaUserExerciseDao jpaUserExerciseDao;
	private final JpaUserExerciseMapper jpaUserExerciseMapper;
	private final JpaUserMapper jpaUserMapper;
	private final JpaExerciseMapper jpaExerciseMapper;

	public JpaUserExerciseRepository(JpaUserDao jpaUserDao, JpaExerciseDao jpaExerciseDao, JpaUserExerciseDao jpaUserExerciseDao, JpaUserMapper jpaUserMapper, JpaExerciseMapper jpaExerciseMapper) {
		this.jpaUserDao = jpaUserDao;
		this.jpaExerciseDao = jpaExerciseDao;
		this.jpaUserExerciseDao = jpaUserExerciseDao;
		this.jpaUserMapper = jpaUserMapper;
		this.jpaExerciseMapper = jpaExerciseMapper;
		this.jpaUserExerciseMapper = new JpaUserExerciseMapper();
	}

	@Override
	public UserExercise save(UserExercise userExercise)
		throws KnowyInconsistentDataException, KnowyExerciseNotFoundException, KnowyUserNotFoundException {

		PublicUserExerciseEntity userSaved = jpaUserExerciseDao.save(jpaUserExerciseMapper.toEntity(userExercise));
		return jpaUserExerciseMapper.toDomain(userSaved);
	}

	@Override
	public Optional<UserExercise> findById(int userId, int exerciseId) {
		return jpaUserExerciseDao.findById(new PublicUserExerciseId(userId, exerciseId))
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public List<UserExercise> findAll() {
		return jpaUserExerciseDao.findAll()
			.stream()
			.map(jpaUserExerciseMapper::toDomain)
			.toList();
	}

	@Override
	public Optional<UserExercise> findNextExerciseByLessonId(int userId, int lessonId) {
		return jpaUserExerciseDao.findNextExerciseByLessonId(userId, lessonId)
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public Optional<UserExercise> findNextExerciseByUserId(int userId) {
		return jpaUserExerciseDao.findNextExerciseByUserId(userId)
			.map(jpaUserExerciseMapper::toDomain);
	}

	@Override
	public Optional<Double> findAverageRateByLessonId(int lessonId) {
		return jpaUserExerciseDao.findAverageRateByLessonId(lessonId);
	}

	public class JpaUserExerciseMapper implements EntityMapper<UserExercise, PublicUserExerciseEntity> {
		@Override
		public UserExercise toDomain(PublicUserExerciseEntity entity) {
			return new UserExercise(
				jpaUserMapper.toDomain(entity.getPublicUserEntity()),
				jpaExerciseMapper.toDomain(entity.getExerciseEntity()),
				entity.getRate(),
				entity.getNextReview()
			);
		}

		@Override
		public PublicUserExerciseEntity toEntity(UserExercise domain) throws KnowyUserNotFoundException, KnowyExerciseNotFoundException {
			return new PublicUserExerciseEntity(
				new PublicUserExerciseId(domain.user().id(), domain.exercise().id()),
				domain.rate(),
				domain.nextReview(),
				jpaUserDao.findById(domain.user().id())
					.orElseThrow(() -> new KnowyUserNotFoundException("User with ID " + domain.user().id() + " not found")),
				jpaExerciseDao.findById(domain.exercise().id())
					.orElseThrow(() -> new KnowyExerciseNotFoundException("Exercise with ID: " + domain.exercise().id() +
						" not found"))
			);
		}
	}
}
