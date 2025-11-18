package home.work.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO, представляющий прогресс студента в курсе.
 */
@Data
@AllArgsConstructor
public class StudentProgress {
    private Double progress;
    private Long submittedAssignments;
    private Long completedQuizzes;
    private Double averageScore;
}
