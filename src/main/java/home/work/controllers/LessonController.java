package home.work.controllers;

import home.work.dto.request.CreateLessonRequest;
import home.work.dto.request.UpdateLessonRequest;
import home.work.dto.simple.LessonSimple;
import home.work.services.LessonService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления уроками в модулях курсов.
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    /**
     * Создать новый урок.
     *
     * @param lesson Данные урока
     * @return Созданный урок
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonSimple.class)))
    @PostMapping
    public ResponseEntity<?> createLesson(
            @Valid @RequestBody CreateLessonRequest lesson) {
        return ResponseEntity.ok(lessonService.createLesson(lesson));
    }

    /**
     * Получить урок по идентификатору вместе с его заданиями.
     *
     * @param id Идентификатор урока
     * @return Урок с заданиями
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonSimple.class)))
    @GetMapping("/{id}")
    public ResponseEntity<?> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonWithAssignments(id));
    }

    /**
     * Обновить существующий урок.
     *
     * @param id            Идентификатор урока
     * @param lessonDetails Обновленные данные урока
     * @return Обновленный урок
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LessonSimple.class)))
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLessonRequest lessonDetails) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDetails));
    }

    /**
     * Получить все уроки для конкретного модуля.
     *
     * @param moduleId Идентификатор модуля
     * @return Список уроков
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LessonSimple.class))))
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<?> getModuleLessons(@PathVariable Long moduleId) {
        return ResponseEntity.ok(lessonService.getModuleLessons(moduleId));
    }

    /**
     * Удалить урок по идентификатору.
     *
     * @param id Идентификатор урока
     * @return Ответ об успешном удалении
     */
    @ApiResponse(responseCode = "204", description = "No content")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Переупорядочить уроки в модуле.
     *
     * @param moduleId  Идентификатор модуля
     * @param lessonIds Список идентификаторов уроков в новом порядке
     * @return Ответ об успешном переупорядочивании
     */
    @ApiResponse(responseCode = "204", description = "No content")
    @PutMapping("/module/{moduleId}/reorder")
    public ResponseEntity<Void> reorderLessons(
            @PathVariable Long moduleId,
            @RequestBody List<Long> lessonIds) {
        lessonService.reorderLessons(moduleId, lessonIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Поиск уроков по названию.
     *
     * @param title Название или часть названия урока
     * @return Список уроков, соответствующих критерию поиска
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LessonSimple.class))))
    @GetMapping("/search")
    public ResponseEntity<?> searchLessons(@RequestParam String title) {
        return ResponseEntity.ok(lessonService.searchLessonsByTitle(title));
    }
}
