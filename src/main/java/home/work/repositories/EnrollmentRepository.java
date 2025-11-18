package home.work.repositories;

import home.work.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Enrollment.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    /**
     * Находит запись о зачислении по идентификаторам студента и курса.
     *
     * @param studentId Идентификатор студента.
     * @param courseId  Идентификатор курса.
     * @return Опциональная запись о зачислении.
     */
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Находит все записи о зачислениях для указанного студента.
     *
     * @param studentId Идентификатор студента.
     * @return Список записей о зачислениях.
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * Находит все записи о зачислениях для указанного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список записей о зачислениях.
     */
    List<Enrollment> findByCourseId(Long courseId);

    /**
     * Проверяет, существует ли запись о зачислении для указанного студента и курса.
     *
     * @param studentId Идентификатор студента.
     * @param courseId  Идентификатор курса.
     * @return true, если запись существует, иначе false.
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * Подсчитывает количество записей о зачислениях для указанного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Количество записей о зачислениях.
     */
    long countByCourseId(Long courseId);
}
