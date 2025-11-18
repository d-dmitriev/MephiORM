package home.work.controllers;

import home.work.dto.request.CreateCategoryRequest;
import home.work.dto.request.UpdateCategoryRequest;
import home.work.dto.simple.CategorySimple;
import home.work.dto.simple.CourseSimple;
import home.work.services.CategoryService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategorySimple.class)))
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoryRequest category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    /**
     * Получить все категории.
     *
     * @return Список категорий
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategorySimple.class))))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategorySimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategorySimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategorySimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseSimple.class))))
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
    @ApiResponse(responseCode = "204", description = "No content")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получить популярные категории по количеству курсов.
     *
     * @param limit Максимальное количество категорий для возврата
     * @return Список популярных категорий
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CategorySimple.class))))
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularCategories(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(categoryService.getPopularCategories(limit));
    }
}
