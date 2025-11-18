package home.work.repositories;

import home.work.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Assignment.
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    /**
     * Находит все задания, связанные с указанным идентификатором урока.
     *
     * @param lessonId Идентификатор урока.
     * @return Список заданий, связанных с уроком.
     */
    List<Assignment> findByLessonId(Long lessonId);

    /**
     * Находит все задания, срок выполнения которых истекает до указанной даты и времени,
     * и которые принадлежат курсу с заданным идентификатором.
     *
     * @param dueDate  Дата и время срока выполнения.
     * @param courseId Идентификатор курса.
     * @return Список заданий, соответствующих критериям.
     */
    List<Assignment> findByDueDateBeforeAndLessonModuleCourseId(LocalDateTime dueDate, Long courseId);

    /**
     * Находит задание по его идентификатору вместе с его связанными отправками.
     *
     * @param id Идентификатор задания.
     * @return Опциональное задание с его отправками.
     */
    @Query("SELECT a FROM Assignment a LEFT JOIN FETCH a.submissions WHERE a.id = :id")
    Optional<Assignment> findByIdWithSubmissions(Long id);

    /**
     * Находит все задания, связанные с указанным идентификатором курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список заданий, связанных с курсом.
     */
    @Query("SELECT a FROM Assignment a WHERE a.lesson.module.course.id = :courseId")
    List<Assignment> findByCourseId(Long courseId);

    /**
     * Находит все просроченные задания для указанного курса.
     *
     * @param now      Текущая дата и время.
     * @param courseId Идентификатор курса.
     * @return Список просроченных заданий.
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :now AND a.lesson.module.course.id = :courseId")
    List<Assignment> findOverdueAssignments(LocalDateTime now, Long courseId);

    /**
     * Подсчитывает количество заданий, связанных с указанным идентификатором курса.
     *
     * @param courseId Идентификатор курса.
     * @return Количество заданий, связанных с курсом.
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.lesson.module.course.id = :courseId")
    Long countByCourseId(Long courseId);
}
