package home.work.repositories;

import home.work.entities.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями CourseReview.
 */
@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourseId(Long courseId);

    List<CourseReview> findByStudentId(Long studentId);

    List<CourseReview> findByRatingGreaterThanEqual(Integer minRating);

    Optional<CourseReview> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT cr FROM CourseReview cr JOIN FETCH cr.student WHERE cr.course.id = :courseId")
    List<CourseReview> findByCourseIdWithStudent(Long courseId);

    @Query("SELECT AVG(cr.rating) FROM CourseReview cr WHERE cr.course.id = :courseId")
    Double findAverageRatingByCourseId(Long courseId);

    @Query("SELECT COUNT(cr) FROM CourseReview cr WHERE cr.course.id = :courseId")
    Long countByCourseId(Long courseId);

    @Query("SELECT cr.course.id, AVG(cr.rating) FROM CourseReview cr GROUP BY cr.course.id HAVING AVG(cr.rating) >= :minRating")
    List<Object[]> findCourseIdsWithAverageRatingAbove(Double minRating);
}
