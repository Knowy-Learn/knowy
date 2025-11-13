package com.knowy.server.infrastructure.controller;

import com.knowy.core.CourseService;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.server.infrastructure.controller.dto.NewsDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PresentationController {

	private final CourseService courseService;

	public PresentationController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping("/")
	public String viewLandingPage(ModelMap interfaceScreen) throws KnowyInconsistentDataException {
		Pagination pagination = new Pagination(0, 3);

		List<NewsDto> newsList = courseService.getAllCourses(pagination)
			.stream()
			.map(NewsDto::fromDomain)
			.toList();

		interfaceScreen.addAttribute("newsList", newsList);
		return "pages/landing-page";
	}
}
