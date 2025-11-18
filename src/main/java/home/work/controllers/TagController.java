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

    @PostMapping
    public ResponseEntity<?> createTag(@Valid @RequestBody CreateTagRequest tag) {
        return ResponseEntity.ok(tagService.createTag(tag));
    }

    @GetMapping
    public ResponseEntity<?> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTags(@RequestParam String query) {
        return ResponseEntity.ok(tagService.searchTags(query));
    }

    @PostMapping("/courses/{courseId}/multiple")
    public ResponseEntity<?> addTagsToCourse(
            @PathVariable Long courseId,
            @RequestBody Set<Long> tagIds) {
        tagService.addTagsToCourse(courseId, tagIds);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tagId}/courses/{courseId}")
    public ResponseEntity<?> removeTagFromCourse(
            @PathVariable Long tagId,
            @PathVariable Long courseId) {
        tagService.removeTagFromCourse(courseId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseTags(@PathVariable Long courseId) {
        return ResponseEntity.ok(tagService.getCourseTags(courseId));
    }

    @GetMapping("/{tagId}/courses")
    public ResponseEntity<?> getTagCourses(@PathVariable Long tagId) {
        return ResponseEntity.ok(tagService.getCoursesByTag(tagId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags() {
        return ResponseEntity.ok(tagService.getPopularTagsWithCounts());
    }
}
