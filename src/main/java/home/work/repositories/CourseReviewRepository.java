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
    /**
     * Находит все отзывы по идентификатору курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список отзывов для указанного курса.
     */
    List<CourseReview> findByCourseId(Long courseId);

    /**
     * Находит все отзывы по идентификатору студента.
     *
     * @param studentId Идентификатор студента.
     * @return Список отзывов, оставленных указанным студентом.
     */
    List<CourseReview> findByStudentId(Long studentId);

    /**
     * Находит все отзывы с рейтингом, большим или равным указанному значению.
     *
     * @param minRating Минимальный рейтинг.
     * @return Список отзывов с рейтингом, большим или равным minRating.
     */
    List<CourseReview> findByRatingGreaterThanEqual(Integer minRating);

    /**
     * Находит отзыв по идентификаторам студента и курса.
     *
     * @param studentId Идентификатор студента.
     * @param courseId  Идентификатор курса.
     * @return Опциональный отзыв, если он существует.
     */
    Optional<CourseReview> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Находит все отзывы для указанного курса вместе с информацией о студенте.
     *
     * @param courseId Идентификатор курса.
     * @return Список отзывов с информацией о студенте.
     */
    @Query("SELECT cr FROM CourseReview cr JOIN FETCH cr.student WHERE cr.course.id = :courseId")
    List<CourseReview> findByCourseIdWithStudent(Long courseId);

    /**
     * Вычисляет средний рейтинг для указанного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Средний рейтинг курса.
     */
    @Query("SELECT AVG(cr.rating) FROM CourseReview cr WHERE cr.course.id = :courseId")
    Double findAverageRatingByCourseId(Long courseId);

    /**
     * Подсчитывает количество отзывов для указанного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Количество отзывов для курса.
     */
    @Query("SELECT COUNT(cr) FROM CourseReview cr WHERE cr.course.id = :courseId")
    Long countByCourseId(Long courseId);

    /**
     * Находит идентификаторы курсов, у которых средний рейтинг выше или равен указанному значению.
     *
     * @param minRating Минимальный средний рейтинг.
     * @return Список массивов объектов, где каждый массив содержит идентификатор курса и его средний рейтинг.
     */
    @Query("SELECT cr.course.id, AVG(cr.rating) FROM CourseReview cr GROUP BY cr.course.id HAVING AVG(cr.rating) >= :minRating")
    List<Object[]> findCourseIdsWithAverageRatingAbove(Double minRating);
}
