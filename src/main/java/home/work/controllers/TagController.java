package home.work.controllers;

import home.work.dto.request.CreateTagRequest;
import home.work.dto.simple.CourseSimple;
import home.work.dto.simple.TagSimple;
import home.work.services.TagService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Контроллер для управления тегами.
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    /**
     * Создает новый тег.
     *
     * @param tag данные для создания тега
     * @return созданный тег
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagSimple.class)))
    @PostMapping
    public ResponseEntity<?> createTag(@Valid @RequestBody CreateTagRequest tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }

    /**
     * Получает все теги.
     *
     * @return список всех тегов
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TagSimple.class))))
    @GetMapping
    public ResponseEntity<?> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    /**
     * Получает тег по идентификатору.
     *
     * @param id идентификатор тега
     * @return тег с указанным идентификатором
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TagSimple.class)))
    @GetMapping("/{id}")
    public ResponseEntity<?> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    /**
     * Ищет теги по заданному запросу.
     *
     * @param query поисковый запрос
     * @return список тегов, соответствующих запросу
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TagSimple.class))))
    @GetMapping("/search")
    public ResponseEntity<?> searchTags(@RequestParam String query) {
        return ResponseEntity.ok(tagService.searchTags(query));
    }

    /**
     * Добавляет теги к курсу.
     *
     * @param courseId идентификатор курса
     * @param tagIds   идентификаторы тегов для добавления
     * @return ответ без содержимого
     */
    @ApiResponse(responseCode = "204", description = "No content")
    @PostMapping("/courses/{courseId}/multiple")
    public ResponseEntity<Void> addTagsToCourse(
            @PathVariable Long courseId,
            @RequestBody Set<Long> tagIds) {
        tagService.addTagsToCourse(courseId, tagIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаляет тег из курса.
     *
     * @param tagId    идентификатор тега
     * @param courseId идентификатор курса
     * @return ответ без содержимого
     */
    @ApiResponse(responseCode = "204", description = "No content")
    @DeleteMapping("/{tagId}/courses/{courseId}")
    public ResponseEntity<Void> removeTagFromCourse(
            @PathVariable Long tagId,
            @PathVariable Long courseId) {
        tagService.removeTagFromCourse(courseId, tagId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает все теги, связанные с курсом.
     *
     * @param courseId идентификатор курса
     * @return список тегов курса
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TagSimple.class))))
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseTags(@PathVariable Long courseId) {
        return ResponseEntity.ok(tagService.getCourseTags(courseId));
    }

    /**
     * Получает все курсы, связанные с тегом.
     *
     * @param tagId идентификатор тега
     * @return список курсов с данным тегом
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CourseSimple.class))))
    @GetMapping("/{tagId}/courses")
    public ResponseEntity<?> getTagCourses(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.getCoursesByTag(tagId));
    }

    /**
     * Удаляет тег по идентификатору.
     *
     * @param id идентификатор тега
     * @return ответ без содержимого
     */
    @ApiResponse(responseCode = "204", description = "No content")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает популярные теги с количеством их использования.
     *
     * @return список популярных тегов с их счетчиками
     */
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "[[\"Python\",1],[\"Hibernate\",1],[\"Java\",1]]")))
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags() {
        return ResponseEntity.ok(tagService.getPopularTagsWithCounts());
    }
}
