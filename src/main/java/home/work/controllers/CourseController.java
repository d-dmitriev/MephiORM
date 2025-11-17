package home.work.controllers;

import home.work.dto.request.CreateCourseRequest;
import home.work.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<?> createCourse(
            @Valid @RequestBody CreateCourseRequest course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithLazyModules(id));
    }

    @GetMapping("/{id}/full")
    public ResponseEntity<?> getCourseFullStructure(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseFullStructure(id));
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<?> getCoursesByCategory(@PathVariable String categoryName) {
        return ResponseEntity.ok(courseService.getCoursesByCategory(categoryName));
    }

    @GetMapping("/tag/{tagName}")
    public ResponseEntity<?> getCoursesByTag(@PathVariable String tagName) {
        return ResponseEntity.ok(courseService.getCoursesByTag(tagName));
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<?> enrollStudent(
            @PathVariable Long courseId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(courseService.enrollStudent(courseId, studentId));
    }

    @PostMapping("/{courseId}/reviews")
    public ResponseEntity<?> addCourseReview(
            @PathVariable Long courseId,
            @RequestParam Long studentId,
            @RequestParam Integer rating,
            @RequestParam String comment) {
        return ResponseEntity.ok(courseService.addCourseReview(courseId, studentId, rating, comment));
    }

    @GetMapping("/{courseId}/reviews")
    public ResponseEntity<?> getCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseReviewWithStudent(courseId));
    }

    @GetMapping("/{courseId}/rating")
    public ResponseEntity<?> getCourseAverageRating(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseAverageRating(courseId));
    }
}
