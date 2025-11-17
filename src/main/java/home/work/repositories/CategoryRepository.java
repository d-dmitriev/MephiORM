package home.work.repositories;

import home.work.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.courses WHERE c.id = :id")
    Optional<Category> findByIdWithCourses(Long id);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.courses")
    List<Category> findAllWithCourses();
}
