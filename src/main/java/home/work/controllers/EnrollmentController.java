package home.work.controllers;

import home.work.services.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления записями о зачислении студентов на курсы.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<?> enrollStudent(
            @RequestParam Long courseId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(enrollmentService.enrollStudentInCourse(courseId, studentId));
    }

    @DeleteMapping
    public ResponseEntity<?> unenrollStudent(
            @RequestParam Long courseId,
            @RequestParam Long studentId) {
        enrollmentService.unenrollStudentFromCourse(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{enrollmentId}/status")
    public ResponseEntity<?> updateEnrollmentStatus(
            @PathVariable Long enrollmentId,
            @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(enrollmentId, status));
    }

    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<?> updateEnrollmentProgress(
            @PathVariable Long enrollmentId,
            @RequestParam Double progress) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentProgress(enrollmentId, progress));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseEnrollments(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }

    @GetMapping("/progress")
    public ResponseEntity<?> calculateStudentProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(enrollmentService.calculateStudentProgress(studentId, courseId));
    }
}
