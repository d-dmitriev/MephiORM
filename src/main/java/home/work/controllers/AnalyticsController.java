package home.work.controllers;

import home.work.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для обработки конечных точек, связанных с аналитикой.
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    /**
     * Получить общую статистику платформы.
     *
     * @return Статистика платформы
     */
    @GetMapping("/platform")
    public ResponseEntity<?> getPlatformStatistics() {
        return ResponseEntity.ok(analyticsService.getPlatformStatistics());
    }

    /**
     * Получить статистику по конкретному курсу.
     *
     * @param courseId Идентификатор курса
     * @return Статистика курса
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<?> getCourseStatistics(@PathVariable Long courseId) {
        return ResponseEntity.ok(analyticsService.getCourseStatistics(courseId));
    }

    /**
     * Получить прогресс конкретного студента по конкретному курсу.
     *
     * @param studentId Идентификатор студента
     * @param courseId  Идентификатор курса
     * @return Прогресс студента
     */
    @GetMapping("/progress")
    public ResponseEntity<?> getStudentProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(analyticsService.getStudentProgress(studentId, courseId));
    }

    /**
     * Получить тренд по количеству регистраций на платформе.
     *
     * @return Тренд регистраций
     */
    @GetMapping("/enrollments/trend")
    public ResponseEntity<Map<String, Long>> getEnrollmentTrend() {
        Map<String, Long> trend = analyticsService.getEnrollmentTrend();
        return ResponseEntity.ok(trend);
    }
}
