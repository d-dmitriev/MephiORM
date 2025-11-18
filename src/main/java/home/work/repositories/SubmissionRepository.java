package home.work.repositories;

import home.work.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByAssignmentId(Long assignmentId);

    List<Submission> findByStudentId(Long studentId);

    List<Submission> findByStudentIdAndAssignmentLessonModuleCourseId(Long studentId, Long courseId);

    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    @Query("SELECT s FROM Submission s JOIN FETCH s.assignment JOIN FETCH s.student WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentIdWithDetails(Long assignmentId);

    @Query("SELECT s FROM Submission s JOIN FETCH s.assignment JOIN FETCH s.student WHERE s.student.id = :studentId")
    List<Submission> findByStudentIdWithDetails(Long studentId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.lesson.module.course.id = :courseId")
    Long countByCourseId(Long courseId);

    @Query("SELECT s FROM Submission s WHERE s.assignment.lesson.module.course.id = :courseId AND s.score IS NULL")
    List<Submission> findUngradedSubmissionsByCourseId(Long courseId);
}
