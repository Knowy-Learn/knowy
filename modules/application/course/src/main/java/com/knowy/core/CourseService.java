package com.knowy.core;

import com.knowy.core.domain.Category;
import com.knowy.core.domain.Course;
import com.knowy.core.domain.Lesson;
import com.knowy.core.domain.UserLesson;
import com.knowy.core.exception.KnowyCourseNotFound;
import com.knowy.core.exception.KnowyCourseSubscriptionException;
import com.knowy.core.exception.KnowyInconsistentDataException;
import com.knowy.core.port.CategoryRepository;
import com.knowy.core.port.CourseRepository;
import com.knowy.core.port.LessonRepository;
import com.knowy.core.port.UserLessonRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserLessonRepository userLessonRepository;
    private final CategoryRepository categoryRepository;

    public CourseService(
            CourseRepository courseRepository,
            LessonRepository lessonRepository,
            UserLessonRepository userLessonRepository,
            CategoryRepository categoryRepository
    ) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.userLessonRepository = userLessonRepository;
        this.categoryRepository = categoryRepository;
    }


    public List<Course> getUserCourses(Integer userId) throws KnowyInconsistentDataException {
        return findCoursesByUserId(userId);
    }

    public List<Course> findCoursesByUserId(Integer userId) throws KnowyInconsistentDataException {
        List<Integer> courseIds = userLessonRepository.findCourseIdsByUserId(userId);
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return courseRepository.findByIdIn(courseIds);
    }

    public List<Course> findAllRandom() {
        return courseRepository.findAllRandom();
    }

    public List<Course> getRecommendedCourses(Integer userId) throws KnowyInconsistentDataException {
        List<Course> userCourses = findCoursesByUserId(userId);

        List<Integer> userCourseIds = userCourses.stream()
                .map(Course::id)
                .toList();

        Set<String> userLanguages = userCourses.stream()
                .flatMap(course -> findLanguagesForCourse(course).stream())
                .collect(Collectors.toSet());

        List<Course> allCourses = findAllCourses()
                .stream()
                .filter(course -> !userCourseIds.contains(course.id()))
                .toList();

        List<Course> langMatching = allCourses.stream()
                .filter(course -> {
                    List<String> courseLanguages = findLanguagesForCourse(course);
                    return courseLanguages.stream().anyMatch(userLanguages::contains);
                }).toList();

        List<Course> recommendations = langMatching
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        if (recommendations.size() < 3) {
            List<Course> remaining = allCourses.stream()
                    .filter(course -> !langMatching.contains(course))
                    .toList();

            for (Course course : remaining) {
                if (recommendations.size() >= 3) {
                    break;
                }
                recommendations.add(course);
            }
        }
        return recommendations;
    }

    public void subscribeUserToCourse(int userId, int courseId) throws KnowyCourseSubscriptionException,
            KnowyInconsistentDataException {
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        if (lessons.isEmpty()) {
            throw new KnowyCourseSubscriptionException("El curso no tiene lecciones disponibles");
        }

        ensureAlreadySubscribed(lessons, userId);

        lessons = lessons.stream()
                .sorted(Comparator.comparing(Lesson::id))
                .toList();

        for (int index = 0; index < lessons.size(); index++) {
            Lesson lesson = lessons.get(index);
            if (!userLessonRepository.existsById(userId, lesson.id())) {
                UserLesson userLesson = new UserLesson(
                        userId,
                        lesson,
                        LocalDate.now(),
                        index == 0 ? UserLesson.ProgressStatus.IN_PROGRESS : UserLesson.ProgressStatus.PENDING
                );
                userLessonRepository.save(userLesson);
            }
        }
    }

    private void ensureAlreadySubscribed(List<Lesson> lessons, Integer userId)
            throws KnowyInconsistentDataException, KnowyCourseSubscriptionException {

        for (Lesson lesson : lessons) {
            if (userLessonRepository.existsByUserIdAndLessonId(userId, lesson.id())) {
                throw new KnowyCourseSubscriptionException("Ya estás suscrito a este curso");
            }
        }
    }

    public List<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public String findCourseImage(Course course) {
        return course.image() != null ? course.image() : "https://picsum.photos/seed/picsum/200/300";
    }

    public List<String> findLanguagesForCourse(Course course) {
        return course.categories().stream()
                .map(Category::name)
                .toList();
    }

    public int getCourseProgress(Integer userId, Integer courseId) {
        int totalLessons = lessonRepository.countByCourseId(courseId);
        if (totalLessons == 0) return 0;
        int completedLessons;
        try {
            completedLessons = userLessonRepository.countByUserIdAndCourseIdAndStatus(
                    userId,
                    courseId,
                    UserLesson.ProgressStatus.COMPLETED
            );
        } catch (KnowyInconsistentDataException e) {
            return -1;
        }
        return (int) Math.round((completedLessons * 100.0 / totalLessons));
    }

    public List<String> findAllLanguages() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::name)
                .toList();
    }

    public Course findById(int id) throws KnowyCourseNotFound {
        return courseRepository.findById(id)
                .orElseThrow(() -> new KnowyCourseNotFound("Not found course with  id: " + id));
    }

    public long getCoursesCompleted(int userId) throws KnowyInconsistentDataException {
        List<Course> userCourses = findCoursesByUserId(userId);
        return userCourses
                .stream()
                .filter(course -> getCourseProgress(userId, course.id()) == 100)
                .count();
    }

    public long getTotalCourses(int userId) throws KnowyInconsistentDataException {
        return findCoursesByUserId(userId).size();
    }

    public long getCoursesPercentage(int userId) throws KnowyInconsistentDataException {
        long totalCourses = getTotalCourses(userId);
        long coursesCompleted = getCoursesCompleted(userId);
        return (totalCourses == 0)
                ? 0
                : (int) Math.round((coursesCompleted * 100.0) / totalCourses);
    }
}
