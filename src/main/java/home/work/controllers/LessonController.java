package home.work.controllers;

import home.work.entities.Lesson;
import home.work.services.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<?> createLesson(
            @RequestParam Long moduleId,
            @Valid @RequestBody Lesson lesson) {
        return ResponseEntity.ok( lessonService.createLesson(moduleId, lesson));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLesson(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonWithAssignments(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody Lesson lessonDetails) {
        return ResponseEntity.ok(lessonService.updateLesson(id, lessonDetails));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<?> getModuleLessons(@PathVariable Long moduleId) {
        return ResponseEntity.ok(lessonService.getModuleLessons(moduleId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/module/{moduleId}/reorder")
    public ResponseEntity<?> reorderLessons(
            @PathVariable Long moduleId,
            @RequestBody List<Long> lessonIds) {
        lessonService.reorderLessons(moduleId, lessonIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchLessons(@RequestParam String title) {
        return ResponseEntity.ok(lessonService.searchLessonsByTitle(title));
    }
}
