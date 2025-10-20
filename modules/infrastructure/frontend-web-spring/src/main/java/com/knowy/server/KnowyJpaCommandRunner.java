package com.knowy.server;

import com.knowy.persistence.adapter.jpa.entity.ExerciseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("java:S106")
public class KnowyJpaCommandRunner implements CommandLineRunner {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== Probando JPQL ===");

		// Tu consulta JPQL
/*		List<PublicUserEntity> entities = em.createQuery(
				"""
					SELECT pu
					FROM LessonEntity l
					JOIN PublicUserLessonEntity pul ON pul.lessonId = l.id
					JOIN PublicUserEntity pu ON pu.id = pul.userId
					WHERE l.id = 1
					""", PublicUserEntity.class)
			.getResultList();

		// Imprime los resultados
		entities.forEach(
			entity -> System.out.println(
				"id: " + entity.getId() +
					" name: " + entity.getNickname()
			)
		);
*/


		System.out.println("---------- Single JPQL Query ----------");
		ExerciseEntity entity = em.createQuery(
			"""
				SELECT e
				FROM OptionEntity o
				JOIN o.exercise e
				WHERE o.id = 1
				""",
			ExerciseEntity.class
		).getSingleResult();
		System.out.println("id: " + entity.getId() + " Statement: " + entity.getQuestion());


	}
}

