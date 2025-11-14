package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.News;
import com.knowy.core.domain.Pagination;
import com.knowy.core.port.NewsRepository;
import com.knowy.persistence.KnowyJpaTestConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = KnowyJpaTestConfiguration.class)
@Testcontainers
class JpaNewsRepositoryTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

	@Autowired
	private NewsRepository jpaNewsRepository;

	@DynamicPropertySource
	static void registerProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
	}

	@BeforeAll
	static void setupDatabase() throws IOException {
		DataSource ds = new DriverManagerDataSource(
			postgres.getJdbcUrl(),
			postgres.getUsername(),
			postgres.getPassword()
		);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);

		Path sqlFile = Path.of("../../../scripts/init-postgresql/sql/00-create-table.sql");
		String sql = Files.readString(sqlFile);
		jdbcTemplate.execute(sql);

		Path sqlFileNews = Path.of("../../../scripts/init-postgresql/sql/04-news.sql");
		String sqlNews = Files.readString(sqlFileNews);
		jdbcTemplate.execute(sqlNews);
	}

	@Test
	void given_newsAndPagination_when_findLastNews_then_returnNews() {
		Pagination pagination = new Pagination(0, 3);

		Iterable<News> result = assertDoesNotThrow(
			() -> jpaNewsRepository.findLastNews(pagination)
		);
		List<News> newsList = StreamSupport.stream(result.spliterator(), false)
			.toList();

		assertAll(
			() -> assertEquals(3, newsList.size()),
			() -> assertTrue(
				newsList.getFirst().date().isAfter(newsList.getLast().date()),
				"The first news item should be more recent than the last one"
			)
		);
	}
}
