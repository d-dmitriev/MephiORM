package home.work.repositories;

import home.work.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления сущностями Category.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Находит категорию по её имени.
     *
     * @param name имя категории
     * @return Optional, содержащий найденную категорию или пустой, если категория не найдена
     */
    Optional<Category> findByName(String name);

    /**
     * Находит категорию по её идентификатору вместе с её связанными курсами.
     *
     * @param id Идентификатор категории.
     * @return Опциональная категория с её курсами.
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.courses WHERE c.id = :id")
    Optional<Category> findByIdWithCourses(Long id);

    /**
     * Находит все категории вместе с их связанными курсами.
     *
     * @return Список всех категорий с их курсами.
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.courses")
    List<Category> findAllWithCourses();
}
