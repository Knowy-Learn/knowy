package com.knowy.server.controller;

import com.knowy.server.controller.dto.LessonPageDataDTO;
import com.knowy.server.controller.dto.SolutionDto;
import com.knowy.server.service.CourseSubscriptionService;
import com.knowy.server.service.model.UserSecurityDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/course")
public class LessonController {

	private final CourseSubscriptionService courseService;

	public LessonController(CourseSubscriptionService courseService) {
		this.courseService = courseService;
	}

	@GetMapping("/{courseId}")
	public String showCourseIntro(@PathVariable Integer courseId, Model model) {
		Integer userId = getLoggedInUserId();

		LessonPageDataDTO data = courseService.getCourseOverviewWithLessons(userId, courseId);

		model.addAttribute("course", data.getCourse());
		model.addAttribute("lessons", data.getCourse().getLessons());
		model.addAttribute("lastLesson", data.getLastLesson());
		model.addAttribute("nextLessonId", data.getNextLessonId());
		model.addAttribute("courseId", courseId);
		model.addAttribute("isIntro", true);

		return "pages/lesson-explanation";
	}

	@GetMapping("/{courseId}/lesson/{lessonId}")
	public String showLesson(@PathVariable Integer courseId,
							 @PathVariable Integer lessonId,
							 Model model) {
		Integer userId = getLoggedInUserId();

		LessonPageDataDTO data = courseService.getLessonViewData(userId, courseId, lessonId);

		model.addAttribute("course", data.getCourse());
		model.addAttribute("lesson", data.getLesson());
		model.addAttribute("lessonContent", data.getLessonContent());
		model.addAttribute("lastLesson", data.getLastLesson());
		model.addAttribute("courseId", courseId);
		model.addAttribute("isIntro", false);

		List<SolutionDto> solutions = new ArrayList<>();
		solutions.add(new SolutionDto("Tarjeta 1: JavaScript vs Java", "Pregunta tarjeta 1", "Solución tarjeta 1"));
		solutions.add(new SolutionDto("Tarjeta 2: PHP", "Pregunta tarjeta 2", "Solución tarjeta 2"));
		solutions.add(new SolutionDto("Tarjeta 3: Mondongo", "Pregunta tarjeta 3", "Solución tarjeta 3"));
		model.addAttribute("solutions", solutions);

		return "pages/lesson-explanation";
	}

	private Integer getLoggedInUserId() {
		UserSecurityDetails userDetails = (UserSecurityDetails)
			SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userDetails.getPublicUser().getId();
	}
}