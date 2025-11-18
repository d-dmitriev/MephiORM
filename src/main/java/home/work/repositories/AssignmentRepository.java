package home.work.repositories;

import home.work.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByLessonId(Long lessonId);

    List<Assignment> findByDueDateBeforeAndLessonModuleCourseId(LocalDateTime dueDate, Long courseId);

    @Query("SELECT a FROM Assignment a LEFT JOIN FETCH a.submissions WHERE a.id = :id")
    Optional<Assignment> findByIdWithSubmissions(Long id);

    @Query("SELECT a FROM Assignment a WHERE a.lesson.module.course.id = :courseId")
    List<Assignment> findByCourseId(Long courseId);

    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :now AND a.lesson.module.course.id = :courseId")
    List<Assignment> findOverdueAssignments(LocalDateTime now, Long courseId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.lesson.module.course.id = :courseId")
    Long countByCourseId(Long courseId);
}
