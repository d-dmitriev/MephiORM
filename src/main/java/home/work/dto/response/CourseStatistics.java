package home.work.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO, представляющий различные статистические данные по курсу.
 */
@Data
@AllArgsConstructor
public class CourseStatistics {
    private Long enrollmentCount;
    private Long assignmentCount;
    private Double averageRating;
    private Long reviewCount;
}
