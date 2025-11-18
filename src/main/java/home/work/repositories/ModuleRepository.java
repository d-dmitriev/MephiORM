package home.work.repositories;

import home.work.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourseId(Long courseId);

    List<Module> findByCourseIdOrderByOrderIndex(Long courseId);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.id = :id")
    Optional<Module> findByIdWithLessons(Long id);

    @Query("SELECT m FROM Module m LEFT JOIN FETCH m.lessons WHERE m.course.id = :courseId ORDER BY m.orderIndex")
    List<Module> findByCourseIdWithLessons(Long courseId);

    Optional<Module> findByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);
}
