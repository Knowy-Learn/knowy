package com.knowy.server;

import com.knowy.core.domain.UserExercise;
import com.knowy.persistence.adapter.jpa.entity.CourseEntity;
import com.knowy.persistence.adapter.jpa.entity.PublicUserExerciseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("java:S106")
public class KnowyJpaCommandRunner implements CommandLineRunner {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== Probando JPQL ===");

/*		// Tu consulta JPQL
		List<PublicUserExerciseEntity> entities = em.createQuery(
				"""
					SELECT pue
					FROM PublicUserExerciseEntity pue
					JOIN pue.exerciseEntity e
					JOIN e.lesson l
					WHERE pue.id.idPublicUser = 1
					  AND l.id = 1
					""", PublicUserExerciseEntity.class)
			.getResultList();

		// Imprime los resultados
		entities.forEach(
			entity -> System.out.println(
				"id: " + entity.getExerciseEntity().getId() +
				" rate: " + entity.getRate()
			)
		);*/


		System.out.println("---------- Single JPQL Query ----------");
		CourseEntity entity = em.createQuery(
				"SELECT l.course FROM LessonEntity l WHERE l.id = 1",
				CourseEntity.class
		).getSingleResult();
		System.out.println("id: " + entity.getId() + " title: " + entity.getTitle() + " author: " + entity.getAuthor());

	}
}

