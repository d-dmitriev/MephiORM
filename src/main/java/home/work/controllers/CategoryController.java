package home.work.controllers;

import home.work.dto.request.CreateCategoryRequest;
import home.work.dto.request.UpdateCategoryRequest;
import home.work.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления категориями курсов.
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Создать новую категорию.
     *
     * @param category Данные категории
     * @return Созданная категория
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoryRequest category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    /**
     * Получить все категории.
     *
     * @return Список категорий
     */
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Получить категорию по идентификатору.
     *
     * @param id Идентификатор категории
     * @return Категория
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    /**
     * Получить категорию по названию.
     *
     * @param name Название категории
     * @return Категория
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCategoryByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getCategoryByName(name));
    }

    /**
     * Обновить категорию.
     *
     * @param id              Идентификатор категории
     * @param categoryDetails Новые данные категории
     * @return Обновленная категория
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest categoryDetails) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDetails));
    }

    /**
     * Получить все курсы в категории.
     *
     * @param id Идентификатор категории
     * @return Список курсов
     */
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getCategoryCourses(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCoursesByCategory(id));
    }

    /**
     * Удалить категорию.
     *
     * @param id Идентификатор категории
     * @return Ответ без содержимого
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получить популярные категории по количеству курсов.
     *
     * @param limit Максимальное количество категорий для возврата
     * @return Список популярных категорий
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularCategories(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(categoryService.getPopularCategories(limit));
    }
}
