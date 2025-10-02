package com.knowy.core.usecase.importer;

import com.knowy.core.Importer;
import com.knowy.core.domain.*;
import com.knowy.core.port.DataLoader;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CoursesImporterUseCase implements Importer<List<Course>> {

	private final DataLoader dataLoader;

	public CoursesImporterUseCase(DataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}

	@Override
	public List<Course> execute(InputStream inputStream) {
		Map<String, Object> data = dataLoader.loadData(inputStream);
		return null;
	}

	/*
	private List<CourseData.InmutableCourseData> mapToCourse(Map<String, Object> data) {
		List<Map<String, Object>> coursesData = ensureList(data.get("courses"));
		List<CourseData.InmutableCourseData> courses = new ArrayList<>();

		for (Map<String, Object> courseMap : coursesData) {
			String title = (String) courseMap.get("title");
			String description = (String) courseMap.get("description");
			String image = (String) courseMap.get("image");
			String author = (String) courseMap.get("author");
			LocalDateTime creationDate = LocalDateTime.now();

			List<String> categoriesData = (List<String>) courseMap.get("categories");
			Set<CategoryData> categories = categoriesData.stream()
				.map(CategoryData.InmutableCategoryData::new)
				.collect(Collectors.toSet());

			List<Map<String, Object>> lessonsData = ensureList(courseMap.get("lessons"));
			Set<LessonData.InmutableLessonData> lessons = mapToLesson(lessonsData);

			courses.add(new CourseData.InmutableCourseData(title, description, image, author, creationDate, categories, lessons));
		}
		return courses;
	}

	private Set<Lesson> mapToLesson(List<Map<String, Object>> lessonsData) {
		List<Lesson> lessons = new ArrayList<>();
		for (Map<String, Object> lessonMap : lessonsData) {
			String title = (String) lessonMap.get("title");
			String explanation = (String) lessonMap.get("explanation");

			List<Map<String, Object>> exercisesData = ensureList(lessonMap.get("exercises"));
			List<Exercise> exercises = mapToExercise(exercisesData);

			lessons.add(new Lesson(title, explanation, exercises));
		}
		return lessons;
	}

	private List<Exercise> mapToExercise(List<Map<String, Object>> exercisesData) {
		List<Exercise> exercises = new ArrayList<>();
		for (Map<String, Object> exerciseMap : exercisesData) {
			String statement = (String) exerciseMap.get("statement");

			List<Map<String, Object>> optionsData = ensureList(exerciseMap.get("options"));
			List<Option> options = mapToOption(optionsData);

			exercises.add(new Exercise(statement, options));
		}
		return exercises;
	}

	private List<Option> mapToOption(List<Map<String, Object>> optionsData) {
		List<Option> options = new ArrayList<>();
		for (Map<String, Object> optionMap : optionsData) {
			String value = (String) optionMap.get("statement");
			String isValid = (String) optionMap.getOrDefault("isValid", false);

			options.add(new Option(value, isValid));
		}
		return options;
	}

*/
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> ensureList(Object obj) {
		if (obj instanceof List) {
			return (List<Map<String, Object>>) obj;
		} else if (obj instanceof Map) {
			return List.of((Map<String, Object>) obj);
		}
		return List.of();
	}
}
