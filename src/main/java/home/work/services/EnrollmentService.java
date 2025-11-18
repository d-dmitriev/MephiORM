package home.work.services;

import home.work.dto.simple.EnrollmentSimple;
import home.work.entities.Enrollment;
import home.work.entities.EnrollmentStatus;
import home.work.entities.UserRole;
import home.work.mappers.EnrollmentMapper;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления записями на курсы (enrollments).
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    private final EnrollmentMapper enrollmentMapper;

    /**
     * Запись студента на курс.
     *
     * @param courseId  идентификатор курса
     * @param studentId идентификатор студента
     * @return информация о записи в упрощенном виде
     */
    @Transactional
    public EnrollmentSimple enrollStudentInCourse(Long courseId, Long studentId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            log.error("Student with id {} is already enrolled in course with id {}", studentId, courseId);
            throw new RuntimeException("Student is already enrolled in this course");
        }

        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.error("Course with id {} not found", courseId);
                    return new RuntimeException("Course not found");
                });
        var student = userRepository.findById(studentId)
                .orElseThrow(() -> {
                    log.error("Student with id {} not found", studentId);
                    return new RuntimeException("Student not found");
                });

        if (student.getRole() != UserRole.STUDENT) {
            log.error("User with id {} is not a student", studentId);
            throw new RuntimeException("Only students can enroll in courses");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setProgress(0.0);

        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    /**
     * Отмена записи студента с курса.
     *
     * @param courseId  идентификатор курса
     * @param studentId идентификатор студента
     */
    @Transactional
    public void unenrollStudentFromCourse(Long courseId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> {
                    log.error("Enrollment for student id {} and course id {} not found", studentId, courseId);
                    return new RuntimeException("Enrollment not found");
                });

        enrollmentRepository.delete(enrollment);
    }

    /**
     * Обновление статуса записи на курс.
     *
     * @param enrollmentId идентификатор записи
     * @param status       новый статус записи
     * @return обновленная информация о записи в упрощенном виде
     */
    @Transactional
    public EnrollmentSimple updateEnrollmentStatus(Long enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> {
                    log.error("Enrollment with id {} not found", enrollmentId);
                    return new RuntimeException("Enrollment not found");
                });

        enrollmentMapper.updateEnrollmentStatus(status, enrollment);
        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    /**
     * Обновление прогресса студента в курсе.
     *
     * @param enrollmentId идентификатор записи
     * @param progress     новый прогресс (от 0.0 до 1.0)
     * @return обновленная информация о записи в упрощенном виде
     */
    @Transactional
    public EnrollmentSimple updateEnrollmentProgress(Long enrollmentId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> {
                    log.error("Enrollment with id {} not found", enrollmentId);
                    return new RuntimeException("Enrollment not found");
                });

        if (progress < 0.0 || progress > 1.0) {
            log.error("Invalid progress value: {}. Must be between 0.0 and 1.0", progress);
            throw new RuntimeException("Progress must be between 0.0 and 1.0");
        }

        enrollmentMapper.updateEnrollmentProgress(progress, enrollment);

        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    /**
     * Получение всех записей на курс.
     *
     * @param courseId идентификатор курса
     * @return список записей в упрощенном виде
     */
    public List<EnrollmentSimple> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream().map(enrollmentMapper::toSimple).toList();
    }

    /**
     * Получение всех записей студента.
     *
     * @param studentId идентификатор студента
     * @return список записей в упрощенном виде
     */
    public List<EnrollmentSimple> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(enrollmentMapper::toSimple).toList();
    }

    /**
     * Получение конкретной записи студента на курс.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return информация о записи в упрощенном виде
     */
    public EnrollmentSimple getEnrollment(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).map(enrollmentMapper::toSimple)
                .orElseThrow(() -> {
                    log.error("Enrollment for student id {} and course id {} not found", studentId, courseId);
                    return new RuntimeException("Enrollment not found");
                });
    }

    /**
     * Вычисление прогресса студента по конкретному курсу.
     *
     * @param studentId идентификатор студента
     * @param courseId  идентификатор курса
     * @return прогресс студента в виде числа от 0.0 до 1.0
     */
    public Double calculateStudentProgress(Long studentId, Long courseId) {
        // Попробуем получить enrollment; если его нет — возвращаем 0.0 вместо бросания исключения,
        var enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollmentOpt.isEmpty()) {
            return 0.0;
        }

        // Calculate progress based on completed lessons, assignments, and quizzes
        Long totalLessons = lessonRepository.countByCourseId(courseId);
        Long completedLessons = 0L; // This would come from a progress tracking entity

        Long totalAssignments = submissionRepository.countByCourseId(courseId);
        Long submittedAssignments = (long) submissionRepository.findByStudentIdAndAssignmentLessonModuleCourseId(studentId, courseId).size();

        Long totalQuizzes = quizSubmissionRepository.countByQuizId(courseId);
        Long completedQuizzes = (long) quizSubmissionRepository.findByStudentIdAndQuizId(studentId, courseId).size();

        if (totalLessons + totalAssignments + totalQuizzes == 0) {
            return 0.0;
        }

        double progress = (completedLessons + submittedAssignments + completedQuizzes) /
                (double) (totalLessons + totalAssignments + totalQuizzes);

        return Math.min(progress, 1.0);
    }
}
