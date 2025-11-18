package home.work.controllers;

import home.work.dto.request.CreateCourseRequest;
import home.work.dto.response.CourseWithModulesResponse;
import home.work.dto.simple.CourseReviewSimple;
import home.work.dto.simple.CourseSimple;
import home.work.dto.simple.EnrollmentSimple;
import home.work.services.CourseService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseSimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseSimple.class))))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseSimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseWithModulesResponse.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseSimple.class))))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseSimple.class))))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnrollmentSimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseReviewSimple.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseReviewSimple.class))))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Double.class)))
    @GetMapping("/{courseId}/rating")
    public ResponseEntity<?> getCourseAverageRating(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseAverageRating(courseId));
    }
}
