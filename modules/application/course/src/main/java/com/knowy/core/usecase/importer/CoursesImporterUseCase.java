package com.knowy.core.usecase.importer;

import com.knowy.core.Importer;
import com.knowy.core.ImporterHelper;
import com.knowy.core.domain.*;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyValidationException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.DataLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.knowy.core.ImporterHelper.PropertyExtractor;
import static com.knowy.core.ImporterHelper.extractorFor;

/**
 * Use case for importing courses from an {@link InputStream}. Converts raw data into domain entity root and its related
 * domain entities.
 */
public class CoursesImporterUseCase implements Importer<List<Course>> {

	private static final String TAG_TITLE = "title";

	private final DataLoader dataLoader;
	private final CourseRepository courseRepository;

	/**
	 * Creates a new {@code CoursesImporterUseCase} with the given data loader.
	 *
	 * @param dataLoader the loader used to parse raw data
	 */
	public CoursesImporterUseCase(DataLoader dataLoader, CourseRepository courseRepository) {
		this.dataLoader = dataLoader;
		this.courseRepository = courseRepository;
	}

	/**
	 * Imports courses from the provided input stream.
	 *
	 * @param inputStream the source of raw course data
	 * @return a list of course data objects
	 * @throws KnowyImporterParseException if parsing fails
	 */
	@Override
	public List<Course> execute(InputStream inputStream, URL schema)
		throws KnowyValidationException, KnowyInconsistentDataException, IOException {

		Map<String, Object> courses = dataLoader.loadData(inputStream, schema);
		System.out.println(courses);

		PropertyExtractor rootPropertyExtractor = extractorFor(courses);
		List<CourseUnidentifiedData> courseUnidentifiedDataList = rootPropertyExtractor
			.extract("course", this::createCourse, ArrayList::new);

		return courseRepository.saveAll(courseUnidentifiedDataList);
	}

	@SuppressWarnings("unchecked")
	private CourseUnidentifiedData createCourse(Map<String, Object> courseMap) throws KnowyImporterParseException {
		String title = ImporterHelper.getRequiredString(courseMap, TAG_TITLE);
		String description = ImporterHelper.getRequiredString(courseMap, "description");
		String image = ImporterHelper.getRequiredString(courseMap, "image");
		String author = ImporterHelper.getRequiredString(courseMap, "author");
		LocalDateTime creationDate = LocalDateTime.now();

		Map<String, Object> categoriesData = (Map<String, Object>) courseMap.get("categories");

		List<String> categoryData = (List<String>) categoriesData.get("category");
		Set<CategoryData> categories = categoryData.stream()
			.map(CategoryData.InmutableCategoryData::new)
			.collect(Collectors.toSet());

		PropertyExtractor coursePropertyExtractor = extractorFor((Map<String, Object>) courseMap.get("lessons"));
		Set<LessonUnidentifiedData> lesson = coursePropertyExtractor
			.extract("lesson", this::createLesson, LinkedHashSet::new);

		return new CourseData.InmutableCourseData(title, description, image, author, creationDate, categories, lesson);
	}

	private LessonUnidentifiedData createLesson(Map<String, Object> lessonMap) throws KnowyImporterParseException {
		String title = ImporterHelper.getRequiredString(lessonMap, TAG_TITLE);
		String explanation = ImporterHelper.getRequiredString(lessonMap, "explanation");

		var documentationData = (Map<String, Object>) lessonMap.get("documentations");
		Set<DocumentationData> documentations = new HashSet<>();
		if (documentationData != null) {
			PropertyExtractor documentationPropertyExtractor = extractorFor(documentationData);
			documentations = documentationPropertyExtractor.extract(
				"documentation", this::createDocumentation, HashSet::new
			);
		}

		PropertyExtractor exercisePropertyExtractor = extractorFor((Map<String, Object>) lessonMap.get("exercises"));
		Set<ExerciseUnidentifiedData> exercises = exercisePropertyExtractor.extract(
			"exercise", this::createExercise, HashSet::new
		);

		return new LessonData.InmutableLessonData(title, explanation, documentations, exercises);
	}

	private DocumentationData createDocumentation(Map<String, Object> documentationMap) throws KnowyImporterParseException {
		String title = ImporterHelper.getRequiredString(documentationMap, TAG_TITLE);
		String link = ImporterHelper.getRequiredString(documentationMap, "link");

		return new DocumentationData.InmutableDocumentationData(title, link);
	}

	private ExerciseUnidentifiedData createExercise(Map<String, Object> exerciseMap) throws KnowyImporterParseException {
		String statement = ImporterHelper.getRequiredString(exerciseMap, "statement");

		PropertyExtractor optionPropertyExtractor = extractorFor((Map<String, Object>) exerciseMap.get("options"));
		List<OptionData> options = optionPropertyExtractor.extract("option", this::createOption, ArrayList::new);

		return new ExerciseData.InmutableExerciseData(statement, options);
	}

	private OptionData createOption(Map<String, Object> optionMap) throws KnowyImporterParseException {
		String value = ImporterHelper.getRequiredString(optionMap, "value");
		String stringIsValid = ImporterHelper.getRequiredString(optionMap, "isValid");

		return new OptionData.InmutableOptionData(value, Boolean.parseBoolean(stringIsValid));
	}
}
