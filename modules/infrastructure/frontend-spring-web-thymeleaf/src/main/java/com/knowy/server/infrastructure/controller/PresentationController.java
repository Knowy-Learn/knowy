package com.knowy.server.infrastructure.controller;

import com.knowy.core.CourseService;
import com.knowy.core.domain.Page;
import com.knowy.core.domain.Pagination;
import com.knowy.core.exception.data.KnowyInconsistentDataException;
import com.knowy.server.infrastructure.controller.dto.NewsDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class PresentationController {

	private final CourseService courseService;

	public PresentationController(CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping("/")
	public String viewLandingPage(ModelMap interfaceScreen) throws KnowyInconsistentDataException {
		Pagination pagination = new Pagination(new Page(0, 3), Optional.empty(), Set.of());

		List<NewsDto> newsList = courseService.getAllCourses(pagination).collection().stream()
			.map(NewsDto::fromDomain)
			.toList();

		interfaceScreen.addAttribute("newsList", newsList);
		return "pages/landing-page";
	}
}
