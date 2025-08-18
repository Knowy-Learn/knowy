package com.knowy.server;

import com.knowy.server.infrastructure.adapters.persistence.entity.CourseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;

import java.util.List;

// @Component
@SuppressWarnings("java:S106")
public class KnowyJpaCommandRunner implements CommandLineRunner {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== Probando JPQL ===");

		// Tu consulta JPQL
		List<CourseEntity> entities = em.createQuery(
				"""
					SELECT distinct c
					FROM CourseEntity c
					    LEFT JOIN c.lessons l
					    LEFT JOIN l.publicUserLessons pul with pul.userId = 1
					WHERE pul.id IS null
					ORDER by c.id
					""", CourseEntity.class)
			.getResultList();

		// Imprime los resultados
		entities.forEach(
			entity -> System.out.println("id: " + entity.getId() + " | title: " + entity.getTitle())
		);
	}
}
