package home.work.controllers;

import home.work.dto.response.CourseStatistics;
import home.work.dto.response.PlatformStatistics;
import home.work.dto.response.StudentProgress;
import home.work.services.AnalyticsService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlatformStatistics.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseStatistics.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProgress.class)))
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
    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(type = "object", example = "{\"2025-11-18\": 150, \"2025-11-19\": 200}")))
    @GetMapping("/enrollments/trend")
    public ResponseEntity<Map<String, Long>> getEnrollmentTrend() {
        Map<String, Long> trend = analyticsService.getEnrollmentTrend();
        return ResponseEntity.ok(trend);
    }
}
