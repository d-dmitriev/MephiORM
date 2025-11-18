package home.work.controllers;

import home.work.dto.request.CreateAssignmentRequest;
import home.work.services.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления заданиями и их отправками.
 */
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    /**
     * Создать новое задание.
     *
     * @param assignment Данные задания
     * @return Созданное задание
     */
    @PostMapping
    public ResponseEntity<?> createAssignment(
            @Valid @RequestBody CreateAssignmentRequest assignment) {
        return ResponseEntity.ok(assignmentService.createAssignment(assignment));
    }

    /**
     * Получить задание по идентификатору вместе с его отправками.
     *
     * @param id Идентификатор задания
     * @return Задание с отправками
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAssignment(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getByIdWithSubmissions(id));
    }

    /**
     * Получить все задания для конкретного урока.
     *
     * @param lessonId Идентификатор урока
     * @return Список заданий
     */
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getLessonAssignments(@PathVariable Long lessonId) {
        return ResponseEntity.ok(assignmentService.getByLessonId(lessonId));
    }

    /**
     * Отправка задания студентом.
     *
     * @param assignmentId Идентификатор задания
     * @param studentId    Идентификатор студента
     * @param content      Содержание отправки
     * @return Информация об отправке
     */
    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<?> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long studentId,
            @RequestParam String content) {
        return ResponseEntity.ok(assignmentService.submitAssignment(assignmentId, studentId, content));
    }

    /**
     * Оценить отправку задания.
     *
     * @param submissionId Идентификатор отправки
     * @param score        Оценка
     * @param feedback     Обратная связь
     * @return Обновленная информация об отправке
     */
    @PutMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Integer score,
            @RequestParam String feedback) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(submissionId, score, feedback));
    }

    /**
     * Получить все отправки для конкретного задания.
     *
     * @param assignmentId Идентификатор задания
     * @return Список отправок
     */
    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<?> getAssignmentSubmissions(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsForAssignment(assignmentId));
    }

    /**
     * Получить все отправки конкретного студента.
     *
     * @param studentId Идентификатор студента
     * @return Список отправок
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentSubmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(assignmentService.getStudentSubmissions(studentId));
    }

    /**
     * Получить все просроченные задания для конкретного курса.
     *
     * @param courseId Идентификатор курса
     * @return Список просроченных заданий
     */
    @GetMapping("/course/{courseId}/overdue")
    public ResponseEntity<?> getOverdueAssignments(@PathVariable Long courseId) {
        return ResponseEntity.ok(assignmentService.getOverdueAssignments(courseId));
    }
}
