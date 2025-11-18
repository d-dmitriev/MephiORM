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
    List<Course> findByCategoryName(String categoryName);

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.teacher LEFT JOIN FETCH c.category WHERE c.id = :id")
    Optional<Course> findByIdWithTeacherAndCategory(Long id);

    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t.name = :tagName")
    List<Course> findByTagName(String tagName);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Optional<Course> findByIdWithModules(Long id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.tags WHERE c.id = :id")
    Optional<Course> findByIdWithTags(Long id);

    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t.id = :tagId")
    List<Course> findByTagId(Long tagId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO course_tag (course_id, tag_id) VALUES (:courseId, :tagId) ON CONFLICT DO NOTHING",
            nativeQuery = true)
    void addTagsToCourseWithClear(Long courseId, Long tagId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM course_tag WHERE course_id = :courseId AND tag_id = :tagId",
            nativeQuery = true)
    void removeTagFromCourse(Long courseId, Long tagId);
}
