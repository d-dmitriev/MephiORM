package home.work.repositories;

import home.work.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Module.
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    /**
     * Находит все модули по идентификатору курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список модулей для указанного курса.
     */
    List<Module> findByCourseId(Long courseId);

    /**
     * Находит все модули по идентификатору курса, упорядоченные по индексу порядка.
     *
     * @param courseId Идентификатор курса.
     * @return Список модулей для указанного курса, упорядоченных по индексу порядка.
     */
    List<Module> findByCourseIdOrderByOrderIndex(Long courseId);

    /**
     * Находит модуль по его идентификатору вместе с его связанными уроками.
     *
     * @param id Идентификатор модуля.
     * @return Опциональный модуль с его уроками.
     */
    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.id = :id")
    Optional<Module> findByIdWithLessons(Long id);

    /**
     * Находит все модули по идентификатору курса вместе с их связанными уроками,
     * упорядоченные по индексу порядка.
     *
     * @param courseId Идентификатор курса.
     * @return Список модулей с их уроками для указанного курса, упорядоченных по индексу порядка.
     */
    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.course.id = :courseId ORDER BY m.orderIndex")
    List<Module> findByCourseIdWithLessons(Long courseId);

    /**
     * Находит модуль по идентификатору курса и индексу порядка.
     *
     * @param courseId   Идентификатор курса.
     * @param orderIndex Индекс порядка модуля.
     * @return Опциональный модуль, если он существует.
     */
    Optional<Module> findByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);
}
