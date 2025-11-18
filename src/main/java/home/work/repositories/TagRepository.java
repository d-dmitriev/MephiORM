package home.work.repositories;

import home.work.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String name);

    @Query("SELECT t FROM Tag t JOIN t.courses c WHERE c.id = :courseId")
    List<Tag> findByCourseId(Long courseId);

    @Query("SELECT t.name, COUNT(c) FROM Tag t JOIN t.courses c GROUP BY t.id, t.name ORDER BY COUNT(c) DESC")
    List<Object[]> findPopularTagsWithCourseCount();
}
