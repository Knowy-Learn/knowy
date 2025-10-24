package com.knowy.core.usecase.importer;

import com.knowy.core.Importer;
import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyValidationException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.DataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CoursesImporterUseCaseTest {

	private static final LocalDateTime FIXED_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);

	@Mock
	private DataLoader dataLoader;

	@Mock
	private CourseRepository courseRepository;

	@InjectMocks
	private CoursesImporterUseCase coursesImporterUseCase;

	@Test
	void given_validCourseData_when_execute_then_mapsToCourseData() throws KnowyInconsistentDataException, IOException, KnowyValidationException {
		Map<String, Object> mockMap = getMockedMap();
		Course courseMock = Mockito.mock(Course.class);

		Mockito.when(dataLoader.loadData(Mockito.any(InputStream.class), Mockito.any(URL.class)))
			.thenReturn(mockMap);
		Mockito.when(courseRepository.saveAll(Mockito.any()))
			.thenReturn(List.of(courseMock));

		try (MockedStatic<LocalDateTime> mockedNow = Mockito.mockStatic(LocalDateTime.class)) {
			mockedNow.when(LocalDateTime::now).thenReturn(FIXED_DATE);

			List<Course> result = assertDoesNotThrow(
				() -> coursesImporterUseCase.execute(Mockito.mock(InputStream.class), Mockito.mock(URL.class))
			);
			assertEquals(List.of(courseMock), result);
			Mockito.verify(courseRepository, Mockito.times(1))
				.saveAll(getMockedCourseUnidentifiedData());
		}
	}

	private Map<String, Object> getMockedMap() {
		Map<String, Object> mockMap = new HashMap<>();

		Map<String, Object> course = new HashMap<>();
		course.put("title", "Java Básico");
		course.put("description", "Curso introductorio para aprender los fundamentos de Java.");
		course.put("image", "java_basico.png");
		course.put("author", "Juan Pérez");
		course.put("categories", List.of("Programación", "Java"));

		Map<String, Object> lesson1 = new HashMap<>();
		lesson1.put("title", "Introducción a Java");
		lesson1.put("explanation", "En esta lección se presenta la historia y usos del lenguaje Java.");
		lesson1.put("documentation", List.of(
				Map.of(
					"title", "Documentación Oficial de Java",
					"link", "https://docs.oracle.com/javase/tutorial/")
			)
		);
		lesson1.put("exercises",
			List.of(Map.of(
				"statement", "¿Cuál de los siguientes es un tipo primitivo en Java?",
				"options", List.of(
					Map.of("value", "Microsoft", "isValid", false),
					Map.of("value", "Sun Microsystems", "isValid", true),
					Map.of("value", "IBM", "isValid", false)
				)
			))
		);

		course.put("lessons", List.of(lesson1));
		mockMap.put("course", List.of(course));
		return mockMap;
	}

	private List<CourseData.InmutableCourseData> getMockedCourseUnidentifiedData() {

		ExerciseData.InmutableExerciseData exercise = new ExerciseData.InmutableExerciseData(
			"¿Cuál de los siguientes es un tipo primitivo en Java?",
			List.of(
				new OptionData.InmutableOptionData("Microsoft", false),
				new OptionData.InmutableOptionData("Sun Microsystems", true),
				new OptionData.InmutableOptionData("IBM", false)
			)
		);

		LessonData.InmutableLessonData lesson = new LessonData.InmutableLessonData(
			"Introducción a Java",
			"En esta lección se presenta la historia y usos del lenguaje Java.",
			Set.of(
				new DocumentationData.InmutableDocumentationData(
					"Documentación Oficial de Java",
					"https://docs.oracle.com/javase/tutorial/")
			),
			Set.of(exercise)
		);

		CourseData.InmutableCourseData course = new CourseData.InmutableCourseData(
			"Java Básico",
			"Curso introductorio para aprender los fundamentos de Java.",
			"java_basico.png",
			"Juan Pérez",
			FIXED_DATE,
			Set.of(
				new CategoryData.InmutableCategoryData("Programación"),
				new CategoryData.InmutableCategoryData("Java")
			),
			Set.of(lesson)
		);

		return List.of(course);
	}

	@Test
	void given_courseDataWithInvalidTags_when_execute_then_throwsKnowySourceParsingException() throws IOException, KnowyValidationException {
		Map<String, Object> mockMap = getMockedInvalidMap();

		Mockito.when(dataLoader.loadData(Mockito.any(InputStream.class), Mockito.any(URL.class)))
			.thenReturn(mockMap);

		try (MockedStatic<LocalDateTime> mockedNow = Mockito.mockStatic(LocalDateTime.class)) {
			mockedNow.when(LocalDateTime::now).thenReturn(FIXED_DATE);

			assertThrows(
				Importer.KnowyImporterParseException.class,
				() -> coursesImporterUseCase.execute(Mockito.mock(InputStream.class), Mockito.mock(URL.class))
			);
		}
	}

	private Map<String, Object> getMockedInvalidMap() {
		Map<String, Object> mockMap = new HashMap<>();

		Map<String, Object> course = new HashMap<>();
		course.put("title", "Java Básico");
		course.put("description", "Curso introductorio para aprender los fundamentos de Java.");
		course.put("image", "java_basico.png");
		course.put("author", "Juan Pérez");
		course.put("categories", List.of("Programación", "Java"));

		Map<String, Object> lesson1 = new HashMap<>();
		lesson1.put("title", "Introducción a Java");
		lesson1.put("explanation", "En esta lección se presenta la historia y usos del lenguaje Java.");
		lesson1.put("documentation", List.of(
				Map.of(
					"title", "Documentación Oficial de Java",
					"link", "https://docs.oracle.com/javase/tutorial/")
			)
		);
		lesson1.put("exercises",
			List.of(Map.of(
				"statement", "¿Cuál de los siguientes es un tipo primitivo en Java?",
				"options", List.of(
					Map.of("value", "Microsoft", "isValid", false),
					Map.of("value", "Sun Microsystems", "isValid", true),
					Map.of("INVALID_VALUE", "IBM", "isValid", false)
				)
			))
		);

		course.put("lessons", List.of(lesson1));
		mockMap.put("courses", List.of(course));
		return mockMap;
	}
}
