package home.work.dto.simple;

import lombok.Data;

@Data
public class EnrollmentSimple {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String status;
    private Double progress;
}
