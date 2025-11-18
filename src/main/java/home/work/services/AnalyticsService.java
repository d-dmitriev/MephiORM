package home.work.services;

import home.work.dto.response.CourseStatistics;
import home.work.dto.response.PlatformStatistics;
import home.work.dto.response.StudentProgress;
import home.work.entities.Enrollment;
import home.work.entities.Submission;
import home.work.entities.UserRole;
import home.work.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для формирования аналитики и статистики образовательной платформы.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final CourseReviewRepository courseReviewRepository;

    /**
     * Получение общей статистики платформы.
     *
     * @return объект PlatformStatistics с общей статистикой
     */
    public PlatformStatistics getPlatformStatistics() {
        Long totalCourses = courseRepository.count();
        Long totalStudents = (long) userRepository.findByRole(UserRole.STUDENT).size();
        Long totalTeachers = (long) userRepository.findByRole(UserRole.TEACHER).size();
        Long totalEnrollments = enrollmentRepository.count();
        Long totalSubmissions = submissionRepository.count();

        return new PlatformStatistics(totalCourses, totalStudents, totalTeachers, totalEnrollments, totalSubmissions);
    }

    /**
     * Получение статистики по конкретному курсу.
     *
     * @param courseId идентификатор курса
     * @return объект CourseStatistics с статистикой по курсу
     */
    public CourseStatistics getCourseStatistics(Long courseId) {
        Long enrollmentCount = enrollmentRepository.countByCourseId(courseId);
        Long assignmentCount = submissionRepository.countByCourseId(courseId);
        Double averageRating = courseReviewRepository.findAverageRatingByCourseId(courseId);
        Long reviewCount = courseReviewRepository.countByCourseId(courseId);

        return new CourseStatistics(enrollmentCount, assignmentCount, averageRating, reviewCount);
    }

    /**
     * Получение прогресса студента по конкретному курсу.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return объект StudentProgress с прогрессом студента
     */
    public StudentProgress getStudentProgress(Long studentId, Long courseId) {
        Double progress = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .map(Enrollment::getProgress)
                .orElse(0.0);

        Long submittedAssignments = (long) submissionRepository.findByStudentIdAndAssignmentLessonModuleCourseId(studentId, courseId).size();
        Long completedQuizzes = (long) quizSubmissionRepository.findByStudentIdAndQuizId(studentId, courseId).size();
        Double averageScore = getUserAverageScore(studentId, courseId);

        return new StudentProgress(progress, submittedAssignments, completedQuizzes, averageScore);
    }

    /**
     * Вычисление среднего балла пользователя по курсу.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return средний балл
     */
    private Double getUserAverageScore(Long studentId, Long courseId) {
        List<Submission> submissions = submissionRepository.findByStudentIdAndAssignmentLessonModuleCourseId(studentId, courseId);
        if (submissions.isEmpty()) {
            return 0.0;
        }

        return submissions.stream()
                .filter(s -> s.getScore() != null)
                .mapToInt(Submission::getScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Получение тренда по количеству новых регистраций на платформе.
     *
     * @return карта с датами и количеством регистраций
     */
    public Map<String, Long> getEnrollmentTrend() {
        // This would typically query enrollments by date
        // Simplified version for demonstration
        return enrollmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getEnrollDate().toLocalDate().toString(),
                        Collectors.counting()
                ));
    }
}