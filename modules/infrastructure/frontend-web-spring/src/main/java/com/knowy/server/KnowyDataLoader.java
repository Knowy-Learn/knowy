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

@Component
@Slf4j
public class KnowyDataLoader {

	private final DataLoader dataLoader;
	private final CourseRepository courseRepository;

	public KnowyDataLoader(DataLoader dataLoader, CourseRepository courseRepository) {
		this.dataLoader = dataLoader;
		this.courseRepository = courseRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void loadCourses() throws KnowyValidationException, KnowyInconsistentDataException, IOException {
		var coursesImporter = new CoursesImporterUseCase(dataLoader, courseRepository);

		coursesImporter.execute(
			new ClassPathResource("importdata/example.xml").getInputStream(),
			new ClassPathResource("schemas/course.xsd").getURL()
		);
	}
}
