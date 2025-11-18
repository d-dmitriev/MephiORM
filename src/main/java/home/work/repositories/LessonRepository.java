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
    List<Lesson> findByModuleId(Long moduleId);

    List<Lesson> findByModuleIdOrderByOrderIndex(Long moduleId);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.id = :id")
    Optional<Lesson> findByIdWithAssignments(Long id);

    @Query("SELECT l FROM Lesson l LEFT JOIN FETCH l.assignments WHERE l.module.id = :moduleId ORDER BY l.orderIndex")
    List<Lesson> findByModuleIdWithAssignments(Long moduleId);

    Optional<Lesson> findByModuleIdAndOrderIndex(Long moduleId, Integer orderIndex);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.module.course.id = :courseId")
    Long countByCourseId(Long courseId);
}
