package home.work.services;

import home.work.dto.simple.EnrollmentSimple;
import home.work.entities.Enrollment;
import home.work.entities.EnrollmentStatus;
import home.work.entities.UserRole;
import home.work.mappers.EnrollmentMapper;
import home.work.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    private final EnrollmentMapper enrollmentMapper;

    @Transactional
    public EnrollmentSimple enrollStudentInCourse(Long courseId, Long studentId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }

        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        var student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new RuntimeException("Only students can enroll in courses");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setProgress(0.0);

        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public void unenrollStudentFromCourse(Long courseId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
    }

    @Transactional
    public EnrollmentSimple updateEnrollmentStatus(Long enrollmentId, String status) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentMapper.updateEnrollmentStatus(status, enrollment);
        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public EnrollmentSimple updateEnrollmentProgress(Long enrollmentId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (progress < 0.0 || progress > 1.0) {
            throw new RuntimeException("Progress must be between 0.0 and 1.0");
        }

        enrollmentMapper.updateEnrollmentProgress(progress, enrollment);

        return enrollmentMapper.toSimple(enrollmentRepository.save(enrollment));
    }

    public List<EnrollmentSimple> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream().map(enrollmentMapper::toSimple).toList();
    }

    public List<EnrollmentSimple> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream().map(enrollmentMapper::toSimple).toList();
    }

    public EnrollmentSimple getEnrollment(Long studentId, Long courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).map(enrollmentMapper::toSimple)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
    }

    public Long getEnrollmentCountForCourse(Long courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    public Double calculateStudentProgress(Long studentId, Long courseId) {
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Enrollment not found");
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

    public List<Enrollment> getCompletedEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED)
                .toList();
    }

    public List<Enrollment> getActiveEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .toList();
    }
}
