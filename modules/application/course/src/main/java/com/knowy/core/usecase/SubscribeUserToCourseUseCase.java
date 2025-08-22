package com.knowy.core.usecase;

import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.domain.UserLesson.ProgressStatus;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Use case for subscribing a user to all the lessons of a course that they are not already subscribed to.
 */
public class SubscribeUserToCourseUseCase {

	private final LessonRepository lessonRepository;
	private final UserLessonRepository userLessonRepository;

	/**
	 * Constructs the use case with the required repositories.
	 *
	 * @param lessonRepository     repository for accessing course lessons
	 * @param userLessonRepository repository for persisting user subscriptions to lessons
	 */
	public SubscribeUserToCourseUseCase(LessonRepository lessonRepository, UserLessonRepository userLessonRepository) {
		this.lessonRepository = lessonRepository;
		this.userLessonRepository = userLessonRepository;
	}

	/**
	 * Executes the subscription process for a user in a given course.
	 * <p>
	 * It retrieves all lessons of the course that the user is not already subscribed to, validates the consistency of
	 * the lesson sequence, and creates new {@link UserLesson} subscriptions. The first lesson is marked as
	 * {@link UserLesson.ProgressStatus#IN_PROGRESS}, while subsequent lessons are marked as
	 * {@link UserLesson.ProgressStatus#PENDING}.
	 *
	 * @param userId   the identifier of the user subscribing to the course
	 * @param courseId the identifier of the course to subscribe to
	 * @throws KnowyCourseSubscriptionException if the user is already subscribed to all lessons in the course, or if
	 *                                          the course has no available lessons
	 * @throws KnowyInconsistentDataException   if no starting lesson is found or if multiple starting lessons exist
	 */
	public void execute(int userId, int courseId)
		throws KnowyCourseSubscriptionException, KnowyInconsistentDataException {

		Set<Lesson> lessons = getCourseLessonsWhereUserIsNotSubscribedTo(userId, courseId);
		Collection<UserLesson> userLessons = createUserLessons(userId, lessons);
		userLessonRepository.saveAll(userLessons);
	}

	private Set<Lesson> getCourseLessonsWhereUserIsNotSubscribedTo(int userId, int courseId)
		throws KnowyCourseSubscriptionException {

		Set<Lesson> courseLessons = new HashSet<>(lessonRepository.findAllByCourseId(courseId));
		Set<Lesson> userLessons = lessonRepository.findAllWhereUserIsSubscribedTo(userId);
		courseLessons.removeAll(userLessons);
		if (courseLessons.isEmpty()) {
			throw new KnowyCourseSubscriptionException(String.format(
				"No lessons were found for the course with id %d to which the user with id %d has not yet subscribed",
				courseId,
				userId
			));
		}
		return courseLessons;
	}

	private List<UserLesson> createUserLessons(int userId, Collection<Lesson> lessons)
		throws KnowyInconsistentDataException {

		Lesson firstLesson = findFirstLesson(lessons);
		Function<Lesson, ProgressStatus> statusResolver = defaultStatusResolver(firstLesson);
		return createUserLessons(userId, lessons, statusResolver);
	}

	private Lesson findFirstLesson(Collection<Lesson> lessons) throws KnowyInconsistentDataException {
		Set<Integer> referencedIds = getReferencedLessonIds(lessons);
		List<Lesson> firstLessonCandidates = findUnreferencedLessons(lessons, referencedIds);

		return switch (firstLessonCandidates.size()) {
			case 0 -> throw new KnowyInconsistentDataException("No starting lesson found");
			case 1 -> firstLessonCandidates.getFirst();
			default -> throw new KnowyInconsistentDataException("Multiple starting lessons found");
		};
	}

	private Set<Integer> getReferencedLessonIds(Collection<Lesson> lessons) {
		return lessons.stream()
			.map(Lesson::nextLessonId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	private List<Lesson> findUnreferencedLessons(Collection<Lesson> lessons, Collection<Integer> referencedIds) {
		return lessons.stream()
			.filter(lesson -> !referencedIds.contains(lesson.id()))
			.toList();
	}

	private Function<Lesson, ProgressStatus> defaultStatusResolver(Lesson firstLesson) {
		return lesson -> lesson == firstLesson
			? ProgressStatus.IN_PROGRESS
			: ProgressStatus.PENDING;
	}

	private List<UserLesson> createUserLessons(
		int userId, Collection<Lesson> lessons, Function<Lesson, ProgressStatus> statusResolver
	) {
		return lessons.stream()
			.map(lesson ->
				new UserLesson(userId, lesson, LocalDate.now(), statusResolver.apply(lesson))
			)
			.sorted(userLessonNextLessonIdComparator())
			.toList();
	}

	private Comparator<UserLesson> userLessonNextLessonIdComparator() {
		return Comparator.comparing(
			(UserLesson ul) -> ul.lesson().nextLessonId(),
			Comparator.nullsLast(Comparator.naturalOrder())
		);
	}
}
