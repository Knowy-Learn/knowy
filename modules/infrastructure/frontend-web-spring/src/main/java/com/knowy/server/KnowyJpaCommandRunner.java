package com.knowy.server;

import com.knowy.persistence.adapter.jpa.entity.LessonEntity;
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

		// Tu consulta JPQL
		List<LessonEntity> entities = em.createQuery(
				"""
					SELECT l
					FROM LessonEntity l
					    INNER JOIN l.publicUserLessons pul
					WHERE l.course.id = 1 AND pul.userId = 1
					""", LessonEntity.class)
			.getResultList();

		// Imprime los resultados
		entities.forEach(
			entity -> System.out.println("id: " + entity.getId() + " | title: " + entity.getTitle())
		);
	}
}
