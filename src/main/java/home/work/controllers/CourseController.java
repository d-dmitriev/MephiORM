package home.work.controllers;

import home.work.dto.request.CreateCourseRequest;
import home.work.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления курсами.
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    /**
     * Создать новый курс.
     *
     * @param course Данные курса
     * @return Созданный курс
     */
    @PostMapping
    public ResponseEntity<?> createCourse(
            @Valid @RequestBody CreateCourseRequest course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    /**
     * Получить все курсы.
     *
     * @return Список курсов
     */
    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * Получить курс по идентификатору вместе с его модулями (ленивые загрузки).
     *
     * @param id Идентификатор курса
     * @return Курс с модулями
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithLazyModules(id));
    }

    /**
     * Получить полный структуру курса по идентификатору (включая модули и уроки).
     *
     * @param id Идентификатор курса
     * @return Полная структура курса
     */
    @GetMapping("/{id}/full")
    public ResponseEntity<?> getCourseFullStructure(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseFullStructure(id));
    }

    /**
     * Получить курсы по категории.
     *
     * @param categoryName Название категории
     * @return Список курсов в категории
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getCoursesByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryName));
    }

    /**
     * Получить курсы по тегу.
     *
     * @param tagName Название тега
     * @return Список курсов с тегом
     */
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<?> getCoursesByTag(@PathVariable String tagName) {
        return ResponseEntity.ok(courseService.getCoursesByTag(tagName));
    }

    /**
     * Записать студента на курс.
     *
     * @param courseId  Идентификатор курса
     * @param studentId Идентификатор студента
     * @return Информация о записи
     */
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<?> enrollStudent(
            @PathVariable Long courseId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(courseService.enrollStudent(courseId, studentId));
    }

    /**
     * Добавить отзыв к курсу.
     *
     * @param courseId  Идентификатор курса
     * @param studentId Идентификатор студента
     * @param rating    Рейтинг
     * @param comment   Комментарий
     * @return Созданный отзыв
     */
    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<?> addCourseReview(
            @PathVariable Long courseId,
            @RequestParam Long studentId,
            @RequestParam Integer rating,
            @RequestParam String comment) {
        return ResponseEntity.ok(courseService.addCourseReview(courseId, studentId, rating, comment));
    }

    /**
     * Получить отзывы курса вместе с информацией о студентах.
     *
     * @param courseId Идентификатор курса
     * @return Список отзывов с информацией о студентах
     */
    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<?> getCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseReviewWithStudent(courseId));
    }

    /**
     * Получить средний рейтинг курса.
     *
     * @param courseId Идентификатор курса
     * @return Средний рейтинг курса
     */
    @GetMapping("/{courseId}/rating")
    public ResponseEntity<?> getCourseAverageRating(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseAverageRating(courseId));
    }
}
