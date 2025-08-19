package com.knowy.core.usecase;

import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SubscribeUserToCourseUseCase {

	private final LessonRepository lessonRepository;
	private final UserLessonRepository userLessonRepository;

	public SubscribeUserToCourseUseCase(LessonRepository lessonRepository, UserLessonRepository userLessonRepository) {
		this.lessonRepository = lessonRepository;
		this.userLessonRepository = userLessonRepository;
	}

	public void execute(int userId, int courseId)
		throws KnowyCourseSubscriptionException, KnowyInconsistentDataException {

		List<Lesson> lessons = getUnsubscribedLessonsByCourseId(userId, courseId);
		subscribeUserToLessons(lessons, userId);
	}

	private List<Lesson> getUnsubscribedLessonsByCourseId(int userId, int courseId) throws KnowyCourseSubscriptionException {
		List<Lesson> lessons = lessonRepository.findAllByCourseIdAndUserUnsubscribed(userId, courseId);
		if (lessons.isEmpty()) {
			throw new KnowyCourseSubscriptionException(String.format(
				"No lessons were found for the course with id %d to subscribe the user with id %d", courseId, userId
			));
		}
		return lessons;
	}

	private void subscribeUserToLessons(List<Lesson> lessons, int userId) throws KnowyInconsistentDataException {
		Lesson firstLesson = findFirstLesson(lessons);
		List<UserLesson> userLessons = createUserLessons(userId, lessons, firstLesson);
		userLessonRepository.saveAll(userLessons);
	}

	private Lesson findFirstLesson(List<Lesson> lessons) throws KnowyInconsistentDataException {
		Set<Integer> referencedIds = lessons.stream()
			.map(Lesson::nextLessonId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		List<Lesson> firstLessons = lessons.stream()
			.filter(lesson -> !referencedIds.contains(lesson.id()))
			.toList();

		return switch (firstLessons.size()) {
			case 0 -> throw new KnowyInconsistentDataException("No starting lesson found");
			case 1 -> firstLessons.getFirst();
			default -> throw new KnowyInconsistentDataException("Multiple starting lessons found");
		};
	}

	private List<UserLesson> createUserLessons(int userId, List<Lesson> lessons, Lesson firstLesson) {
		return lessons.stream()
			.map(lesson -> createUserLesson(userId, lesson, firstLesson))
			.toList();
	}

	private UserLesson createUserLesson(int userId, Lesson lesson, Lesson firstLesson) {
		UserLesson.ProgressStatus status = lesson.equals(firstLesson)
			? UserLesson.ProgressStatus.IN_PROGRESS
			: UserLesson.ProgressStatus.PENDING;

		return new UserLesson(userId, lesson, LocalDate.now(), status);
	}
}
