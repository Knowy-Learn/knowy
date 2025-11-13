package com.knowy.server;

import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.exception.KnowyValidationException;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.DataLoader;
import com.knowy.core.usecase.importer.CoursesImporterUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Component responsible for loading initial course data when the application starts.
 */
@Component
@Slf4j
public class KnowyDataLoader {

	private final DataLoader dataLoader;
	private final CourseRepository courseRepository;

	/**
	 * Constructs a new {@code KnowyDataLoader} with the specified dependencies.
	 *
	 * @param dataLoader       the {@link DataLoader} used to load XML data
	 * @param courseRepository the {@link CourseRepository} used to persist courses
	 */
	public KnowyDataLoader(DataLoader dataLoader, CourseRepository courseRepository) {
		this.dataLoader = dataLoader;
		this.courseRepository = courseRepository;
	}

	/**
	 * Loads initial courses from the XML file and persists them when the application is ready.
	 * <p>
	 * This method is triggered by the {@link ApplicationReadyEvent}.
	 *
	 * @throws KnowyValidationException       if the XML data does not conform to the schema
	 * @throws KnowyInconsistentDataException if the data is inconsistent with expected rules
	 * @throws IOException                    if an I/O error occurs while reading the files
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void loadCourses() throws KnowyValidationException, KnowyInconsistentDataException, IOException {
		var coursesImporter = new CoursesImporterUseCase(dataLoader, courseRepository);

		coursesImporter.execute(
			new ClassPathResource("importdata/example.xml").getInputStream(),
			new ClassPathResource("schemas/course.xsd").getURL()
		);
	}
}
