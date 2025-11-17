package home.work.controllers;

import home.work.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/platform")
    public ResponseEntity<AnalyticsService.PlatformStatistics> getPlatformStatistics() {
        AnalyticsService.PlatformStatistics statistics = analyticsService.getPlatformStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<AnalyticsService.CourseStatistics> getCourseStatistics(@PathVariable Long courseId) {
        AnalyticsService.CourseStatistics statistics = analyticsService.getCourseStatistics(courseId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/progress")
    public ResponseEntity<AnalyticsService.StudentProgress> getStudentProgress(
            @RequestParam Long studentId,
            @RequestParam Long courseId) {
        AnalyticsService.StudentProgress progress = analyticsService.getStudentProgress(studentId, courseId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/enrollments/trend")
    public ResponseEntity<Map<String, Long>> getEnrollmentTrend() {
        Map<String, Long> trend = analyticsService.getEnrollmentTrend();
        return ResponseEntity.ok(trend);
    }
}
