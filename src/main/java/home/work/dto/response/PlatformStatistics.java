package home.work.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO, представляющий общие статистические данные по платформе онлайн-обучения.
 */
@Data
@AllArgsConstructor
public class PlatformStatistics {
    private Long totalCourses;
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalEnrollments;
    private Long totalSubmissions;
}
