package com.knowy.persistence.adapter.jpa;

import com.knowy.core.domain.*;
import com.knowy.core.port.CourseRepository;
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
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@ContextConfiguration(classes = KnowyJpaTestConfiguration.class)
class JpaCourseRepositoryTest {

	private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

	@Autowired
	private CourseRepository jpaCourseRepository;

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
	}

	@Test
	void given_courseData_when_save_then_returnCourseDomain() {

		CourseUnidentifiedData courseInput1 = createInputCourse(1, List.of(1, 2));
		CourseUnidentifiedData courseInput2 = createInputCourse(2, List.of(3, 4));

		List<Course> result = assertDoesNotThrow(
			() -> jpaCourseRepository.saveAll(List.of(courseInput1, courseInput2))
		);

		assertAll(
			() -> assertEquals(2, result.size(), "There must be 2 courses"),
			() -> assertCourse(result, 1, List.of(1, 2)),
			() -> assertCourse(result, 2, List.of(3, 4))
		);
	}

	private ExerciseUnidentifiedData createInputExercise(int num) {
		OptionData option1 = new OptionData.InmutableOptionData("Option value " + num, true);
		OptionData option2 = new OptionData.InmutableOptionData("Option value " + (num + 1), false);

		return new ExerciseData.InmutableExerciseData(
			"Exercise statement " + num,
			List.of(option1, option2)
		);
	}

	private LessonUnidentifiedData createInputLesson(int num) {
		DocumentationData documentation1 = new DocumentationData.InmutableDocumentationData(
			"Documentation title " + num,
			"Documentation link " + num
		);
		DocumentationData documentation2 = new DocumentationData.InmutableDocumentationData(
			"Documentation title " + (num + 1),
			"Documentation link " + (num + 1)
		);
		Set<DocumentationData> documentations = new LinkedHashSet<>();
		documentations.add(documentation1);
		documentations.add(documentation2);

		return new LessonData.InmutableLessonData(
			"Lesson title " + num,
			"Lesson explanation " + num,
			documentations,
			Set.of(createInputExercise(num))
		);
	}

	private CourseUnidentifiedData createInputCourse(int courseId, List<Integer> lessonsId) {
		CategoryData category1 = new CategoryData.InmutableCategoryData("Category " + courseId);
		CategoryData category2 = new CategoryData.InmutableCategoryData("Category " + (courseId + 1));

		return new CourseData.InmutableCourseData(
			"Course Title " + courseId,
			"Course Description " + courseId,
			"Course Image " + courseId,
			"Course Author " + courseId,
			FIXED_DATE,
			Set.of(category1, category2),
			Set.of(createInputLesson(lessonsId.getFirst()), createInputLesson(lessonsId.getLast()))
		);
	}

	private void assertCourse(List<Course> courses, int expectedId, List<Integer> expectedLessonsId) {
		Course course = courses.stream()
			.filter(c -> c.title().equals("Course Title " + expectedId))
			.findFirst()
			.orElseThrow(() -> new AssertionError("Course 'Course Title " + expectedId + "' not found"));

		assertAll(
			() -> assertEquals(expectedId, course.id(), "The course ID must be " + expectedId),
			() -> assertCategory(course),
			() -> assertLesson(course, expectedLessonsId.getFirst()),
			() -> assertLesson(course, expectedLessonsId.getLast())
		);
	}

	private void assertCategory(Course course) {
		Set<Category> categories = course.categories();

		assertAll(
			() -> assertEquals(2, course.categories().size()),
			() -> assertTrue(categories.stream()
					.allMatch(cat -> cat != null && cat.id() >= 0),
				"All categories must be non-null and have a valid ID"
			)
		);
	}

	private void assertLesson(Course course, int expectedId) {
		String expectedTitle = "Lesson title " + expectedId;

		Lesson lesson = course.lessons().stream()
			.filter(l -> expectedTitle.equals(l.title()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("Lesson " + expectedId + " not found in course " + course.id()));

		assertEquals(2, course.lessons().size(), "Course should have exactly 2 lessons");

		assertAll("Validating lesson of course " + course.id(),
			() -> assertEquals(course.id(), lesson.courseId(), "Lesson must belong to the course"),
			() -> assertEquals(expectedTitle, lesson.title(), "Lesson title must match expected value"),
			() -> assertLessonDocumentations(lesson),
			() -> assertExercise(lesson)
		);
	}

	private void assertLessonDocumentations(Lesson lesson) {
		Set<Documentation> documentations = lesson.documentations();

		assertAll("Documentation for lesson " + lesson.id(),
			() -> assertEquals(2, documentations.size(), "Lesson must have exactly 2 documentations"),
			() -> assertTrue(
				documentations.stream().allMatch(doc -> doc != null && doc.id() >= 0),
				"All documentations must be non-null and have a valid ID"
			)
		);
	}

	private void assertExercise(Lesson lesson) {
		Exercise exercise = lesson.exercises().stream()
			.findFirst()
			.orElseThrow(() -> new AssertionError("Exercise not found"));

		assertAll("Exercise of Lesson of Course " + lesson.courseId(),
			() -> assertEquals(1, lesson.exercises().size(), "There must be 1 exercise"),
			() -> assertTrue(exercise != null && exercise.id() >= 0),
			() -> assertOptions(Objects.requireNonNull(exercise))
		);
	}

	private void assertOptions(Exercise exercise) {
		List<Option> options = exercise.options();

		assertAll("Options of Exercise of Lesson " + exercise.lessonId(),
			() -> assertEquals(2, options.size(), "There must be 2 options"),
			() -> assertTrue(
				options.stream().allMatch(opt -> opt != null && opt.id() >= 0),
				"All options must be non-null and have a valid ID"
			)
		);
	}
}
