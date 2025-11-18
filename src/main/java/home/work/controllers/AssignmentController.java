package home.work.controllers;

import home.work.dto.request.CreateAssignmentRequest;
import home.work.services.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<?> createAssignment(
            @Valid @RequestBody CreateAssignmentRequest assignment) {
        return ResponseEntity.ok(assignmentService.createAssignment(assignment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getByIdWithSubmissions(id));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getLessonAssignments(@PathVariable Long lessonId) {
        return ResponseEntity.ok(assignmentService.getByLessonId(lessonId));
    }

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam String content) {
        return ResponseEntity.ok(assignmentService.submitAssignment(assignmentId, studentId, content));
    }

    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Integer score,
            @RequestParam String feedback) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(submissionId, score, feedback));
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<?> getAssignmentSubmissions(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsForAssignment(assignmentId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentSubmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(assignmentService.getStudentSubmissions(studentId));
    }

    @GetMapping("/course/{courseId}/overdue")
    public ResponseEntity<?> getOverdueAssignments(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getOverdueAssignments(courseId));
    }
}
