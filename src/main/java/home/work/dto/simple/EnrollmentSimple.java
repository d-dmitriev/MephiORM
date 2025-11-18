package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Enrollment.
 */
@Data
public class EnrollmentSimple {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String status;
    private Double progress;
}
