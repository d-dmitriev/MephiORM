package home.work.repositories;

import home.work.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Course.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    /**
     * Находит курсы по имени категории.
     *
     * @param categoryName имя категории
     * @return список курсов, относящихся к указанной категории
     */
    List<Course> findByCategoryName(String categoryName);

    /**
     * Находит курсы по идентификатору учителя.
     *
     * @param teacherId идентификатор учителя
     * @return список курсов, преподаваемых указанным учителем
     */
    List<Course> findByTeacherId(Long teacherId);

    /**
     * Находит курсы, название которых содержит указанную строку (без учета регистра).
     *
     * @param title часть названия курса
     * @return список курсов, название которых содержит указанную строку
     */
    List<Course> findByTitleContainingIgnoreCase(String title);

    /**
     * Находит курс по его идентификатору вместе с его связанными учителем и категорией.
     *
     * @param id Идентификатор курса.
     * @return Опциональный курс с его учителем и категорией.
     */
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.teacher LEFT JOIN FETCH c.category WHERE c.id = :id")
    Optional<Course> findByIdWithTeacherAndCategory(Long id);

    /**
     * Находит курсы по имени тега.
     *
     * @param tagName имя тега
     * @return список курсов, связанных с указанным тегом
     */
    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t.name = :tagName")
    List<Course> findByTagName(String tagName);

    /**
     * Находит курс по его идентификатору вместе с его связанными модулями.
     *
     * @param id Идентификатор курса.
     * @return Опциональный курс с его модулями.
     */
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Optional<Course> findByIdWithModules(Long id);

    /**
     * Находит курс по его идентификатору вместе с его связанными тегами.
     *
     * @param id Идентификатор курса.
     * @return Опциональный курс с его тегами.
     */
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.tags WHERE c.id = :id")
    Optional<Course> findByIdWithTags(Long id);

    /**
     * Находит курсы по идентификатору тега.
     *
     * @param tagId идентификатор тега
     * @return список курсов, связанных с указанным тегом
     */
    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t.id = :tagId")
    List<Course> findByTagId(Long tagId);

    /**
     * Добавляет тег к курсу.
     *
     * @param courseId идентификатор курса
     * @param tagId    идентификатор тега
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO course_tag (course_id, tag_id) VALUES (:courseId, :tagId) ON CONFLICT DO NOTHING",
            nativeQuery = true)
    void addTagsToCourseWithClear(Long courseId, Long tagId);

    /**
     * Удаляет тег из курса.
     *
     * @param courseId идентификатор курса
     * @param tagId    идентификатор тега
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM course_tag WHERE course_id = :courseId AND tag_id = :tagId",
            nativeQuery = true)
    void removeTagFromCourse(Long courseId, Long tagId);
}
