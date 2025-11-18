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

    /**
     * Зачислить студента на курс.
     *
     * @param courseId  Идентификатор курса
     * @param studentId Идентификатор студента
     * @return Информация о зачислении
     */
    @PostMapping
    public ResponseEntity<?> enrollStudent(
            @RequestParam Long courseId,
            @RequestParam Long studentId) {
        return ResponseEntity.ok(enrollmentService.enrollStudentInCourse(courseId, studentId));
    }

    /**
     * Отчислить студента с курса.
     *
     * @param courseId  Идентификатор курса
     * @param studentId Идентификатор студента
     * @return Пустой ответ
     */
    @DeleteMapping
    public ResponseEntity<?> unenrollStudent(
            @RequestParam Long courseId,
            @RequestParam Long studentId) {
        enrollmentService.unenrollStudentFromCourse(courseId, studentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновить статус зачисления студента на курс.
     *
     * @param enrollmentId Идентификатор зачисления
     * @param status       Новый статус
     * @return Обновленная информация о зачислении
     */
    @PutMapping("/{enrollmentId}/status")
    public ResponseEntity<?> updateEnrollmentStatus(
            @PathVariable Long enrollmentId,
            @RequestParam String status) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentStatus(enrollmentId, status));
    }

    /**
     * Обновить прогресс студента на курсе.
     *
     * @param enrollmentId Идентификатор зачисления
     * @param progress     Новый прогресс (в процентах)
     * @return Обновленная информация о зачислении
     */
    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<?> updateEnrollmentProgress(
            @PathVariable Long enrollmentId,
            @RequestParam Double progress) {
        return ResponseEntity.ok(enrollmentService.updateEnrollmentProgress(enrollmentId, progress));
    }

    /**
     * Получить все зачисления на конкретный курс.
     *
     * @param courseId Идентификатор курса
     * @return Список зачислений
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseEnrollments(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getCourseEnrollments(courseId));
    }

    /**
     * Получить все зачисления конкретного студента.
     *
     * @param studentId Идентификатор студента
     * @return Список зачислений
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentEnrollments(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentService.getStudentEnrollments(studentId));
    }

    /**
     * Рассчитать прогресс студента на курсе.
     *
     * @param studentId Идентификатор студента
     * @param courseId  Идентификатор курса
     * @return Прогресс студента на курсе
     */
    @GetMapping("/progress")
    public ResponseEntity<?> calculateStudentProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(enrollmentService.calculateStudentProgress(studentId, courseId));
    }
}
