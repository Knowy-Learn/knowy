package com.knowy.server.service;

import com.knowy.server.entity.CourseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserHomeService {
	private final CourseSubscriptionService courseSubscriptionService;

	public UserHomeService(CourseSubscriptionService courseSubscriptionService) {
		this.courseSubscriptionService = courseSubscriptionService;
	}

	public long getCoursesCompleted(int userId) {
		List<CourseEntity> userCourses = courseSubscriptionService.findCoursesByUserId(userId);
		return userCourses.stream().filter(c-> courseSubscriptionService.getCourseProgress(userId, c.getId())==100).count();
	}

	public long getTotalCourses(int userId) {
		return courseSubscriptionService.findCoursesByUserId(userId).size();
	}

	public long getCoursesPercentage(int userId) {
		long totalCourses = getTotalCourses(userId);
		long coursesCompleted = getCoursesCompleted(userId);
		return (totalCourses == 0)
			? 0
			: (int) Math.round((coursesCompleted * 100.0) / totalCourses);
	}
}
