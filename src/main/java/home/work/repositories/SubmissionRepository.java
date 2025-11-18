package home.work.repositories;

import home.work.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Submission.
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    /**
     * Находит все отправки по идентификатору задания.
     *
     * @param assignmentId Идентификатор задания.
     * @return Список отправок для указанного задания.
     */
    List<Submission> findByAssignmentId(Long assignmentId);

    /**
     * Находит все отправки по идентификатору студента.
     *
     * @param studentId Идентификатор студента.
     * @return Список отправок для указанного студента.
     */
    List<Submission> findByStudentId(Long studentId);

    /**
     * Находит отправки по идентификаторам студента и курса.
     *
     * @param studentId Идентификатор студента.
     * @param courseId  Идентификатор курса.
     * @return Список отправок, соответствующих критериям.
     */
    List<Submission> findByStudentIdAndAssignmentLessonModuleCourseId(Long studentId, Long courseId);

    /**
     * Находит отправку по идентификаторам студента и задания.
     *
     * @param studentId    Идентификатор студента.
     * @param assignmentId Идентификатор задания.
     * @return Опциональная отправка, если она существует.
     */
    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);

    /**
     * Находит все отправки вместе с информацией о задании и студенте.
     *
     * @param assignmentId Идентификатор задания.
     * @return Список отправок с деталями задания и студента.
     */
    @Query("SELECT s FROM Submission s JOIN FETCH s.assignment JOIN FETCH s.student WHERE s.assignment.id = :assignmentId")
    List<Submission> findByAssignmentIdWithDetails(Long assignmentId);

    /**
     * Находит все отправки вместе с информацией о задании и студенте.
     *
     * @param studentId Идентификатор студента.
     * @return Список отправок с деталями задания и студента.
     */
    @Query("SELECT s FROM Submission s JOIN FETCH s.assignment JOIN FETCH s.student WHERE s.student.id = :studentId")
    List<Submission> findByStudentIdWithDetails(Long studentId);

    /**
     * Подсчитывает количество отправок для заданного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Количество отправок.
     */
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.lesson.module.course.id = :courseId")
    Long countByCourseId(Long courseId);

    /**
     * Находит все не проверенные отправки для заданного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список неподготовленных отправок.
     */
    @Query("SELECT s FROM Submission s WHERE s.assignment.lesson.module.course.id = :courseId AND s.score IS NULL")
    List<Submission> findUngradedSubmissionsByCourseId(Long courseId);
}
