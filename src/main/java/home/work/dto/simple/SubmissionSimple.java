package home.work.dto.simple;

import lombok.Data;

/**
 * Упрощённый DTO для сущности Submission.
 */
@Data
public class SubmissionSimple {
    private Long id;
    private String content;
    private Long assignmentId;
    private Long studentId;
    private Integer score;
    private String feedback;
}
