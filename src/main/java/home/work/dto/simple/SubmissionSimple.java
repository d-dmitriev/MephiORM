package home.work.dto.simple;

import lombok.Data;

@Data
public class SubmissionSimple {
    private Long id;
    private String content;
    private Long assignmentId;
    private Long studentId;
    private Integer score;
    private String feedback;
}
