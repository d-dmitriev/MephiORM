package home.work.repositories;

import home.work.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Lesson.
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    /**
     * Находит все уроки по идентификатору модуля.
     *
     * @param moduleId Идентификатор модуля.
     * @return Список уроков для указанного модуля.
     */
    List<Lesson> findByModuleId(Long moduleId);

    /**
     * Находит все уроки по идентификатору модуля, упорядоченные по индексу порядка.
     *
     * @param moduleId Идентификатор модуля.
     * @return Список уроков для указанного модуля, упорядоченных по индексу порядка.
     */
    List<Lesson> findByModuleIdOrderByOrderIndex(Long moduleId);

    /**
     * Находит урок по идентификатору вместе с его связанными заданиями.
     *
     * @param id Идентификатор урока.
     * @return Опциональный урок с его заданиями.
     */
    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.id = :id")
    Optional<Lesson> findByIdWithAssignments(Long id);

    /**
     * Находит все уроки по идентификатору модуля вместе с их связанными заданиями,
     * упорядоченные по индексу порядка.
     *
     * @param moduleId Идентификатор модуля.
     * @return Список уроков с их заданиями для указанного модуля, упорядоченных по индексу порядка.
     */
    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.module.id = :moduleId ORDER BY l.orderIndex")
    List<Lesson> findByModuleIdWithAssignments(Long moduleId);

    /**
     * Находит урок по идентификатору модуля и индексу порядка.
     *
     * @param moduleId   Идентификатор модуля.
     * @param orderIndex Индекс порядка урока.
     * @return Опциональный урок, если он существует.
     */
    Optional<Lesson> findByModuleIdAndOrderIndex(Long moduleId, Integer orderIndex);

    /**
     * Подсчитывает количество уроков, связанных с указанным идентификатором курса.
     *
     * @param courseId Идентификатор курса.
     * @return Количество уроков, связанных с курсом.
     */
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.module.course.id = :courseId")
    Long countByCourseId(Long courseId);
}
