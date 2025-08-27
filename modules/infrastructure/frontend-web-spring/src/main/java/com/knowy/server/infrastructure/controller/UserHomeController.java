package com.knowy.server.infrastructure.controller;

import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.CourseService;
import com.knowy.core.usecase.course.GetAllCoursesWithProgressResult;
import com.knowy.server.infrastructure.security.UserSecurityDetails;
import com.knowy.server.infrastructure.controller.dto.CourseBannerDTO;
import com.knowy.server.infrastructure.controller.dto.MissionsDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserHomeController {

	private final CourseService courseService;

	public UserHomeController(
		CourseService courseService) {
		this.courseService = courseService;
	}

	@GetMapping("/home")
	public String userHome(Model model, @AuthenticationPrincipal UserSecurityDetails userDetails) throws KnowyInconsistentDataException {
		Integer userId = userDetails.getUser().id();

		List<GetAllCoursesWithProgressResult> coursesWithProgress = courseService.getAllCourseProgress(userId);

		long coursesCompleted = coursesWithProgress.stream()
			.filter(course -> course.progress() == 100f)
			.count();
		long totalCourses = coursesWithProgress.size();

		boolean hasCourses = totalCourses > 0;

		double percent = hasCourses
			? ((double) coursesCompleted / totalCourses) * 100
			: 0;

		List<CourseBannerDTO> banners = courseService.findAllInRandomOrder()
			.stream()
			.limit(4)
			.map(CourseBannerDTO::fromDomain)
			.toList();

		List<MissionsDto> missionsList = getMissionsDto();

		model.addAttribute("hasCourses", hasCourses);
		model.addAttribute("completedCourses", coursesCompleted);
		model.addAttribute("totalCourses", totalCourses);
		model.addAttribute("fractionProgress", percent);
		model.addAttribute("newsHome", banners);
		model.addAttribute("username", userDetails.getUser().nickname());

		model.addAttribute("missionsList", missionsList);
		return "pages/user-home";
	}

	private List<MissionsDto> getMissionsDto() {
		List<MissionsDto> missionsList = new ArrayList<>();
		MissionsDto mission1 = new MissionsDto();
		mission1.setName("Completa 3 lecciones");
		mission1.setCurrentProgress(2);
		mission1.setTotalProgress(3);

		missionsList.add(mission1);
		MissionsDto mission2 = new MissionsDto();
		mission2.setName("Completa 4 lecciones");
		mission2.setCurrentProgress(2);
		mission2.setTotalProgress(4);

		missionsList.add(mission2);
		MissionsDto mission3 = new MissionsDto();
		mission3.setName("Completa 5 lecciones");
		mission3.setCurrentProgress(2);
		mission3.setTotalProgress(5);

		missionsList.add(mission3);
		return missionsList;
	}
}
