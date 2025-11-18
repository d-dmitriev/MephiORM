package home.work.controllers;

import home.work.dto.request.CreateTagRequest;
import home.work.services.TagService;
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
    @PostMapping
    public ResponseEntity<?> createTag(@Valid @RequestBody CreateTagRequest tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }

    /**
     * Получает все теги.
     *
     * @return список всех тегов
     */
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
    @PostMapping("/courses/{courseId}/multiple")
    public ResponseEntity<?> addTagsToCourse(
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
    @DeleteMapping("/{tagId}/courses/{courseId}")
    public ResponseEntity<?> removeTagFromCourse(
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает популярные теги с количеством их использования.
     *
     * @return список популярных тегов с их счетчиками
     */
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags() {
        return ResponseEntity.ok(tagService.getPopularTagsWithCounts());
    }
}
